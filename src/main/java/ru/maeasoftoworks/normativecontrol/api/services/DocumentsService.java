package ru.maeasoftoworks.normativecontrol.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.repositories.DocumentsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentsService {
    private final DocumentsRepository documentsRepository;
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
}
