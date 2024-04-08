package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.VerificationStatus;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.CreateDocumentDto;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.DocumentVerdictDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UnauthorizedException;
import ru.maeasoftoworks.normativecontrol.api.repositories.AcademicGroupsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.DocumentsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.ResultsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentsService {
    private final DocumentsRepository documentsRepository;
    private final ResultsRepository resultsRepository;
    private final UsersRepository usersRepository;
    private final AcademicGroupsRepository academicGroupsRepository;

    public List<Document> getDocuments(Admin admin) {
        University university = admin.getUniversity();
        List<User> users = usersRepository.findUsersByUniversity(university);
        List<Document> result = new ArrayList<>();
        for (User user : users) {
            result.addAll(documentsRepository.findDocumentsByUser(user));
        }
        return result;
    }

    @Transactional
    public Result createDocument(User user, CreateDocumentDto createDocumentDto) {
        Document document;

        if (user.getRole() == Role.NORMOCONTROLLER) {
            AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(createDocumentDto.getAcademicGroupId());
            if (academicGroup.getUniversity() != user.getUniversity())
                throw new UnauthorizedException("You don't have access to this resource");
            document = Document.builder()
                    .user(user)
                    .studentName(createDocumentDto.getStudentName())
                    .academicGroup(academicGroup)
                    .fileName(createDocumentDto.getDocumentName())
                    .isReported(false)
                    .comment(null)
                    .build();
        } else {
            document = Document.builder()
                    .user(user)
                    .studentName(getShortenedNameForUser(user))
                    .academicGroup(((Student) user).getAcademicGroup())
                    .fileName(createDocumentDto.getDocumentName())
                    .isReported(false)
                    .comment(null)
                    .build();
        }

        documentsRepository.save(document);

        Result result = new Result(document, VerificationStatus.PENDING);
        resultsRepository.save(result);

        return result;
    }

    @Transactional
    public void deleteDocument(Admin admin, Long documentId) {
        Document document = documentsRepository.findDocumentById(documentId);
        if (admin.getUniversity() != document.getUser().getUniversity())
            throw new UnauthorizedException("You don't have access to this resource");
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
        document.setComment(documentVerdictDto.getMessage());
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

    private String getShortenedNameForUser(User user) {
        String lastName = user.getLastName();
        String firstnameInitial = String.valueOf(user.getFirstName().charAt(0));
        String middlenameInitial = String.valueOf(user.getMiddleName().charAt(0));
        return lastName + " " + firstnameInitial + "." + middlenameInitial + ".";
    }
}
