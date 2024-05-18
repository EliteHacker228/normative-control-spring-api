package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;

import java.util.List;

public interface DocumentsRepository extends JpaRepository<Document, Long> {
    List<Document> findDocumentsByStudent(Student student);

    List<Document> findDocumentsByStudentId(Long studentId);

    Document findTopByStudentIdOrderByVerificationDateDesc(Long studentId);

    Document findDocumentById(Long documentId);

    Integer countAllByStudentId(Long studentId);
}
