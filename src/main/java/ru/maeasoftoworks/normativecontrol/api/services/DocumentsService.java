package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.VerificationStatus;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.CreateDocumentDto;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.DocumentVerdictDto;
import ru.maeasoftoworks.normativecontrol.api.mq.Message;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;
import ru.maeasoftoworks.normativecontrol.api.repositories.*;
import ru.maeasoftoworks.normativecontrol.api.s3.S3;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
        if (user.getRole() == Role.STUDENT)
            return documentsRepository.findDocumentsByStudent((Student) user);
        else if (user.getRole() == Role.NORMOCONTROLLER) {
            List<Student> students = studentsRepository.findStudentsByAcademicGroupNormocontrollerId(user.getId());
            List<Document> result = new ArrayList<>();
            for (Student student : students) {
                result.addAll(documentsRepository.findDocumentsByStudent(student));
            }
            return result;
        } else if (user.getRole() == Role.ADMIN) {
            return documentsRepository.findAll();
        }

        return List.of();
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

        String csvHeader = "ФИО,Группа,Название работы,Результат проверки,Дата и время первой загрузки,Количество попыток";
        List<String> documentsCsv = new ArrayList<>();
        documentsCsv.add(csvHeader);
        for (Student student : students) {
            List<Document> documents = documentsRepository.findDocumentsByStudent(student);
            if(documents.isEmpty())
                break;
            Document document = documents.getFirst();
            String fio = MessageFormat.format("{0} {1} {2}", student.getLastName(), student.getFirstName(), student.getMiddleName());
            String academicGroupName = student.getAcademicGroup().getName();
            String documentName = document.getFileName();
            String verificationResult = document.getDocumentVerdict().name();

            SimpleDateFormat sf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            String firstUploadingDateTime = sf.format(document.getVerificationDate());
            String attempts = documentsRepository.countAllByStudentId(document.getStudent().getId()).toString();

            String csvRow = MessageFormat.format("{0},{1},{2},{3},{4},{5}", fio, academicGroupName, documentName, verificationResult,firstUploadingDateTime ,attempts);
            documentsCsv.add(csvRow);
        }

        return String.join("\n", documentsCsv);
    }

    @Transactional
    @SneakyThrows
    public Result createDocument(User user1, CreateDocumentDto createDocumentDto) {
        Student user = (Student) user1;
        Document document = Document.builder()
                .student(user)
                .fileName(normalizeFileName(createDocumentDto.getDocumentName()))
                .isReported(false)
                .comment(null)
                .build();

        documentsRepository.save(document);
        String documentName = user.getId() + "/" + document.getId() + "/source.docx";
        s3.putObject(createDocumentDto.getDocument().getInputStream(), documentName);

        Result result = new Result(document, VerificationStatus.PENDING);
        resultsRepository.save(result);

        String docxResultName = user.getId() + "/" + document.getId() + "/result.docx";
        String htmlResultName = user.getId() + "/" + document.getId() + "/result.html";
        Message message = new Message(document.getId(), documentName, docxResultName, htmlResultName);
        mqPublisher.publishToVerify(message.getAsJsonString());

        return result;
    }

    @SneakyThrows
    public byte[] getDocument(User user, Long documentId, String documentType) {
        Document targetDocument = documentsRepository.findDocumentById(documentId);
        user = targetDocument.getStudent();
        String documentPath = user.getId() + "/" + documentId + "/result." + documentType;
        try (ByteArrayOutputStream result = s3.getObject(documentPath)) {
            byte[] bytes = result.toByteArray();
            return bytes;
        }
    }

    public Document getDocumentNode(User user, Long documentId) {
        Document targetDocument = documentsRepository.findDocumentById(documentId);
        return targetDocument;
    }


    @Transactional
    public void deleteDocument(Admin admin, Long documentId) {
        Document document = documentsRepository.findDocumentById(documentId);
        documentsRepository.delete(document);
    }

    public VerificationStatus getDocumentsVerificationStatus(Long documentId) {
        Document document = documentsRepository.findDocumentById(documentId);
        return resultsRepository.findResultByDocument(document).getVerificationStatus();
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
    public Document reportOnDocument(Long documentId) {
        Document document = documentsRepository.findDocumentById(documentId);
        document.setReported(true);
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

    private String getShortenedNameForUser(User user) {
        String lastName = user.getLastName();
        String firstnameInitial = String.valueOf(user.getFirstName().charAt(0));
        String middlenameInitial = String.valueOf(user.getMiddleName().charAt(0));
        return lastName + " " + firstnameInitial + "." + middlenameInitial + ".";
    }

    private String normalizeFileName(String fileName) {
        if (fileName.endsWith(".docx"))
            return fileName;
        else
            return fileName + ".docx";
    }
}
