package ru.maeasoftoworks.normativecontrol.api.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.maeasoftoworks.normativecontrol.api.entities.Document;
import ru.maeasoftoworks.normativecontrol.api.integrations.s3.S3;
import ru.maeasoftoworks.normativecontrol.api.jobpools.DocumentsVerificationPool;
import ru.maeasoftoworks.normativecontrol.api.mq.DocumentMessageBody;
import ru.maeasoftoworks.normativecontrol.api.mq.MqConfiguration;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;
import ru.maeasoftoworks.normativecontrol.api.repositories.DocumentsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.isVerified.IsVerifiedRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.isVerified.IsVerifiedResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.verification.VerificationRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.verification.VerificationResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.verifiedDocument.VerifiedDocumentRequest;
import ru.maeasoftoworks.normativecontrol.api.utils.CorrelationIdUtils;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.io.*;

@RestController
@RequestMapping("/documents/open")
@RequiredArgsConstructor
@Slf4j
public class OpenDocumentsController {

    private final MqPublisher mqPublisher;
    private final S3 s3;
    private final MqConfiguration mqConfiguration;
    private final DocumentsVerificationPool documentsVerificationPool;
    private final DocumentsRepository documentsRepository;
    private final UsersRepository usersRepository;
    private final JwtUtils jwtUtils;

    @PostMapping("/verification")
    @Transactional
    public ResponseEntity<String> sendToVerification(@Valid VerificationRequest verificationRequest) throws IOException {
        if (verificationRequest.getDocument().isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), "Empty files not allowed");
        }
        DocumentMessageBody resultFileName = uploadFile(verificationRequest.getDocument().getInputStream());
        if (resultFileName == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Uploading of file has failed");
        }
        mqPublisher.publishToVerify(resultFileName.getAsJsonString(), resultFileName.getCorrelationId());
        VerificationResponse response = new VerificationResponse(resultFileName.getCorrelationId());
        documentsVerificationPool.startVerification(resultFileName.getCorrelationId());

        Document document = new Document(null, System.currentTimeMillis());
        document.setFingerprint(verificationRequest.getFingerprint());
        document.setCorrelationId(resultFileName.getCorrelationId());
        documentsRepository.save(document);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getAsJsonString());
    }

    @GetMapping("/isVerified")
    public ResponseEntity<String> isDocumentVerified(@Valid IsVerifiedRequest isVerifiedRequest) {
        IsVerifiedResponse isVerifiedResponse = new IsVerifiedResponse("Document with id " + isVerifiedRequest.getDocumentId() + " is verified or absent");
        if (documentsVerificationPool.isVerificationInProgress(isVerifiedRequest.getDocumentId())) {
            isVerifiedResponse = new IsVerifiedResponse("Document with id " + isVerifiedRequest.getDocumentId() + " is not verified");
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isVerifiedResponse.getAsJsonString());
    }

    @GetMapping("/verifiedDocument")
    @SneakyThrows
    public ResponseEntity<byte[]> getVerifiedDocument(@Valid VerifiedDocumentRequest verifiedDocumentRequest) {
        Document document = documentsRepository.findByFingerprintAndCorrelationId(verifiedDocumentRequest.getFingerprint(), verifiedDocumentRequest.getDocumentId());
        try (ByteArrayOutputStream result = s3.getObject(document.getCorrelationId() + "/result." + verifiedDocumentRequest.getDocumentType())) {
            if (result != null) {
                val bytes = result.toByteArray(); // todo BLOCKING
                if (verifiedDocumentRequest.getDocumentType().equals("docx")) {
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(bytes);
                }
                if (verifiedDocumentRequest.getDocumentType().equals("html")) {
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.TEXT_HTML)
                            .body(bytes);
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Result of type " + verifiedDocumentRequest.getDocumentType() + " is not exists");
        }
    }

    private DocumentMessageBody uploadFile(InputStream inputStream) {
        try {
            String fileName = "source.docx";
            String correlationId = CorrelationIdUtils.generateCorrelationId();
            DocumentMessageBody documentMessageBody = new DocumentMessageBody(correlationId, fileName, mqConfiguration.getReceiverQueueName());
            s3.putObject(inputStream, documentMessageBody.getDocument());
            return documentMessageBody;
        } catch (Exception e) {
            log.warn("Error occurred: " + e);
            return null;
        }
    }
}
