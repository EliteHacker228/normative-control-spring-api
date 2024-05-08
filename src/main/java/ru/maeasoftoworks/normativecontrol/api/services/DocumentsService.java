package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.VerificationStatus;
import ru.maeasoftoworks.normativecontrol.api.domain.users.*;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.CreateDocumentDto;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.DocumentReportDto;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.DocumentVerdictDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceNotFoundException;
import ru.maeasoftoworks.normativecontrol.api.mq.Message;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;
import ru.maeasoftoworks.normativecontrol.api.repositories.*;
import ru.maeasoftoworks.normativecontrol.api.s3.S3;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// TODO: Возвращать 404 если ресурс не найден
@Service
@RequiredArgsConstructor
public class DocumentsService {
    private final DocumentsRepository documentsRepository;
    private final ResultsRepository resultsRepository;
    private final UsersRepository usersRepository;
    private final StudentsRepository studentsRepository;
    private final AcademicGroupsRepository academicGroupsRepository;
    private final S3 s3;
    private final MqPublisher mqPublisher;

    public List<Document> getDocuments(User user) {
        if (user.getRole() == Role.STUDENT) {
            return documentsRepository.findDocumentsByStudent((Student) user).stream()
                    .filter(document -> resultsRepository.findResultByDocument(document).getVerificationStatus() != VerificationStatus.ERROR).toList();
        } else if (user.getRole() == Role.NORMOCONTROLLER) {
            List<Student> students = studentsRepository.findStudentsByAcademicGroupNormocontrollerId(user.getId());
            List<Document> result = new ArrayList<>();
            for (Student student : students) {
                List<Document> documents = documentsRepository.findDocumentsByStudent(student).stream()
                        .filter(document -> resultsRepository.findResultByDocument(document).getVerificationStatus() != VerificationStatus.ERROR).toList();
                result.addAll(documents);
            }
            return result;
        } else if (user.getRole() == Role.ADMIN) {
            List<Document> documents = documentsRepository.findAll().stream()
                    .filter(document -> resultsRepository.findResultByDocument(document).getVerificationStatus() != VerificationStatus.ERROR).toList();
            return documents;
        }

        return List.of();
    }

    public List<Document> getActualDocuments(Normocontroller normocontroller) {
        List<Student> pupils = studentsRepository.findStudentsByAcademicGroupNormocontrollerId(normocontroller.getId());
        List<Document> result = new ArrayList<>();
        for (Student pupil : pupils) {
            Document pupilsActualDocument = documentsRepository.findTopByStudentIdOrderByVerificationDateDesc(pupil.getId());
            if(pupilsActualDocument == null)
                continue;
            result.add(pupilsActualDocument);
        }
        return result;
    }

    public String getDocumentsCsv(User user) {
        List<Student> students = new ArrayList<>();
        if (user.getRole() == Role.NORMOCONTROLLER) {
            List<Student> foundStudents = studentsRepository.findStudentsByAcademicGroupNormocontrollerId(user.getId());
            students.addAll(foundStudents);
        } else if (user.getRole() == Role.ADMIN) {
            List<Student> foundStudents = studentsRepository.findAll();
            students.addAll(foundStudents);
        }

        String csvHeader = "ФИО,Группа,Название работы,Результат проверки,Дата и время   первой загрузки,Количество попыток";
        List<String> documentsCsv = new ArrayList<>();
        documentsCsv.add(csvHeader);
        for (Student student : students) {
            List<Document> documents = documentsRepository.findAll().stream()
                    .filter(document -> resultsRepository.findResultByDocument(document).getVerificationStatus() != VerificationStatus.ERROR).toList();
            if (documents.isEmpty())
                break;
            Document document = documents.getFirst();
            String fio = student.getFullName();
            String academicGroupName = student.getAcademicGroup().getName();
            String documentName = document.getFileName();
            String verificationResult = document.getDocumentVerdict().name();

            SimpleDateFormat sf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            String firstUploadingDateTime = sf.format(document.getVerificationDate());
            String attempts = String.valueOf(documents.stream().count());

            String csvRow = MessageFormat.format("{0},{1},{2},{3},{4},{5}", fio, academicGroupName, documentName, verificationResult, firstUploadingDateTime, attempts);
            documentsCsv.add(csvRow);
        }

        return String.join("\n", documentsCsv);
    }

    @Transactional
    @SneakyThrows
    public Document createDocument(User user1, CreateDocumentDto createDocumentDto) {
        Student user = (Student) user1;
        Document document = Document.builder()
                .student(user)
                .fileName(normalizeFileName(createDocumentDto.getDocumentName()))
                .isReported(false)
                .comment(null)
                .build();

        Result result = new Result(document, VerificationStatus.PENDING);
        document.setResult(result);
        documentsRepository.save(document);

        result.setDocument(document);
        resultsRepository.save(result);

        String documentName = user.getId() + "/" + document.getId() + "/source.docx";
        s3.putObject(createDocumentDto.getDocument().getInputStream(), documentName);

        String docxResultName = user.getId() + "/" + document.getId() + "/result.docx";
        String htmlResultName = user.getId() + "/" + document.getId() + "/result.html";
        Message message = new Message(user.getId(), document.getId());
        mqPublisher.publishToVerify(message.getAsJsonString());

        return document;
    }

    @SneakyThrows
    public byte[] getDocument(User user, Long documentId, String documentType) {
        Document targetDocument = documentsRepository.findDocumentById(documentId);
        Result result = resultsRepository.findResultByDocument(targetDocument);
        if (result.getVerificationStatus() == VerificationStatus.ERROR) {
            String message = MessageFormat.format("Document with id {0} does not exists", documentId);
            throw new ResourceNotFoundException(message);
        }
        user = targetDocument.getStudent();
        String documentPath = user.getId() + "/" + documentId + "/result." + documentType;
        try (ByteArrayOutputStream resultBytes = s3.getObject(documentPath)) {
            byte[] bytes = resultBytes.toByteArray();
            return bytes;
        }
    }

    public Document getDocumentNode(User user, Long documentId) {
        Document targetDocument = documentsRepository.findDocumentById(documentId);
        return targetDocument;
    }

    public Result getResultNode(Long documentId) {
        return resultsRepository.findResultByDocumentId(documentId);
    }


    @Transactional
    public void deleteDocument(Admin admin, Long documentId) {
        Document document = documentsRepository.findDocumentById(documentId);
        documentsRepository.delete(document);
    }

    public Result getDocumentVerificationStatus(Long documentId) {
        Document document = documentsRepository.findDocumentById(documentId);
        return resultsRepository.findResultByDocument(document);
    }

    @Transactional
    public Document setVerdictOnDocument(Long documentId, DocumentVerdictDto documentVerdictDto) {
        Document document = documentsRepository.findDocumentById(documentId);
        document.setDocumentVerdict(documentVerdictDto.getVerdict());
        document.setComment(documentVerdictDto.getComment());
        documentsRepository.save(document);
        return document;
    }

    @Transactional
    public Document reportOnDocument(Long documentId, DocumentReportDto documentReportDto) {
        Document document = documentsRepository.findDocumentById(documentId);
        if (!document.isReported())
            document.setReported(true);
        Set<String> reportedMistakes = document.getReportedMistakesIds();
        reportedMistakes.add(documentReportDto.getMistakeId());
        documentsRepository.save(document);
        return document;
    }

    @Transactional
    public Document unreportOnDocument(Long documentId, DocumentReportDto documentReportDto) {
        Document document = documentsRepository.findDocumentById(documentId);
        Set<String> reportedMistakes = document.getReportedMistakesIds();
        reportedMistakes.remove(documentReportDto.getMistakeId());
        if (reportedMistakes.isEmpty())
            document.setReported(false);
        documentsRepository.save(document);
        return document;
    }

    @Transactional
    public Document makeVerdictOnDocument(Long documentId, DocumentVerdictDto documentVerdictDto) {
        Document document = documentsRepository.findDocumentById(documentId);
        document.setDocumentVerdict(documentVerdictDto.getVerdict());
        document.setComment(documentVerdictDto.getComment());
        documentsRepository.save(document);
        return document;
    }

    private String normalizeFileName(String fileName) {
        if (fileName.endsWith(".docx"))
            return fileName;
        else
            return fileName + ".docx";
    }
}
