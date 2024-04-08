package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;

import java.util.List;

public interface DocumentsRepository extends JpaRepository<Document, Long> {
    List<Document> findDocumentsByUser(User user);

    Document findDocumentById(Long documentId);
}
