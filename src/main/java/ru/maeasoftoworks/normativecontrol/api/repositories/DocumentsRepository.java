package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.entities.Document;

import java.util.List;

public interface DocumentsRepository extends JpaRepository<Document, Long> {
    Document findByFingerprintAndCorrelationId(String fingerprint, String correlationId);

    boolean existsByFingerprint(String fingerprint);
}

