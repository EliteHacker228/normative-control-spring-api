package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.VerificationStatus;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.CreateDocumentDto;
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
            document = Document.builder()
                    .user(user)
                    .studentName(createDocumentDto.getStudentName())
                    .fileName(createDocumentDto.getDocumentName())
                    .isReported(false)
                    .comment(null)
                    .build();
        } else {
            document = Document.builder()
                    .user(user)
                    .studentName(getShortenedNameForUser(user))
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

    private String getShortenedNameForUser(User user){
        String lastName = user.getLastName();
        String firstnameInitial = String.valueOf(user.getFirstName().charAt(0));
        String middlenameInitial = String.valueOf(user.getMiddleName().charAt(0));
        return lastName + " " + firstnameInitial + "." + middlenameInitial + ".";
    }
}
