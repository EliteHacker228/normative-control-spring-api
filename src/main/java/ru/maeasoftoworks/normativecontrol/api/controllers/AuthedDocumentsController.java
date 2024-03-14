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
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.Document;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.integrations.s3.S3;
import ru.maeasoftoworks.normativecontrol.api.jobpools.DocumentsVerificationPool;
import ru.maeasoftoworks.normativecontrol.api.mq.DocumentMessageBody;
import ru.maeasoftoworks.normativecontrol.api.mq.MqConfiguration;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;
import ru.maeasoftoworks.normativecontrol.api.repositories.DocumentsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.authed.verification.VerificationAuthedRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.authed.verifiedDocument.VerifiedDocumentAuthedRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.authed.verifiedDocument.VerifiedDocumentAuthedResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.open.verification.VerificationOpenResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.open.verifiedDocument.VerifiedDocumentOpenRequest;
import ru.maeasoftoworks.normativecontrol.api.utils.CorrelationIdUtils;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/documents/authed")
@RequiredArgsConstructor
@Slf4j
public class AuthedDocumentsController {

    private final MqPublisher mqPublisher;
    private final S3 s3;
    private final MqConfiguration mqConfiguration;
    private final DocumentsVerificationPool documentsVerificationPool;
    private final DocumentsRepository documentsRepository;
    private final UsersRepository usersRepository;
    private final JwtUtils jwtUtils;

    @PostMapping("/verification")
    @Transactional
    public ResponseEntity<String> sendToVerification(@RequestHeader("Authorization") String bearerToken, @Valid VerificationAuthedRequest verificationOpenRequest) throws IOException {
        if (verificationOpenRequest.getDocument().isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), "Empty files not allowed");
        }
        DocumentMessageBody resultFileName = uploadFile(verificationOpenRequest.getDocument().getInputStream());
        if (resultFileName == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Uploading of file has failed");
        }
        mqPublisher.publishToVerify(resultFileName.getAsJsonString(), resultFileName.getCorrelationId());
        VerificationOpenResponse response = new VerificationOpenResponse(resultFileName.getCorrelationId());
        documentsVerificationPool.startVerification(resultFileName.getCorrelationId());

        String accessToken = bearerToken.substring("Bearer ".length());
        String userEmail = jwtUtils.getClaimsFromAccessTokenString(accessToken).getPayload().getSubject();
        User user = usersRepository.findByEmail(userEmail);
        Document document = new Document(user, System.currentTimeMillis());
        document.setCorrelationId(resultFileName.getCorrelationId());
        documentsRepository.save(document);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getAsJsonString());
    }

    @GetMapping("/verifiedDocument")
    @SneakyThrows
    public ResponseEntity<byte[]> getVerifiedDocument(@RequestHeader("Authorization") String bearerToken, @Valid VerifiedDocumentAuthedRequest verificationAuthedRequest) {
        String accessToken = bearerToken.substring("Bearer ".length());
        String userEmail = jwtUtils.getClaimsFromAccessTokenString(accessToken).getPayload().getSubject();
        User user = usersRepository.findByEmail(userEmail);
        Document document;
        if (user.getRole() == Role.STUDENT) {
            document = documentsRepository.findByUserAndCorrelationId(user, verificationAuthedRequest.getDocumentId());
        } else {
            document = documentsRepository.findByCorrelationId(verificationAuthedRequest.getDocumentId());
        }

        if (user.getRole() == Role.STUDENT && document.getUser().getRole() == Role.STUDENT && !user.getId().equals(document.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no access for this document");
        }
        try (ByteArrayOutputStream result = s3.getObject(document.getCorrelationId() + "/result." + verificationAuthedRequest.getDocumentType())) {
            if (result != null) {
                val bytes = result.toByteArray(); // todo BLOCKING
                if (verificationAuthedRequest.getDocumentType().equals("docx")) {
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(bytes);
                }
                if (verificationAuthedRequest.getDocumentType().equals("html")) {
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.TEXT_HTML)
                            .body(bytes);
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Result of type " + verificationAuthedRequest.getDocumentType() + " is not exists");
        }
    }

    // TODO: Controller: list, выдаёт данные о работе, которую проверял юзер

    @GetMapping("/list")
    public ResponseEntity<String> getLisOfVerificationsForUser() {
        return null;
    }

    // TODO: Controller: find, выдаёт список работ по запрошенным параметрам
    // email: adfadf@urfu.me
    // group: RI-400004
    // name: Кузнецов М. А.
    // afterDate: DD.MM.YYYY:HH:MM:SS (нижняя граница дипазона дат)
    // beforeDate: DD.MM.YYYY:HH:MM:SS (верхняя граница дипазона дат)

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
