package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;

import java.util.List;

public interface ResultsRepository extends JpaRepository<Result, Long> {
    Result findResultByDocument(Document document);
    Result findResultByDocumentId(Long documentId);
    List<Result> findResultsByDocument_StudentId(Long studentId);
}
