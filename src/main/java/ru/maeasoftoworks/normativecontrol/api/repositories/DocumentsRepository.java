package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.entities.Document;

public interface DocumentsRepository extends JpaRepository<Document, Long> {
}
