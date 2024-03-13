package ru.maeasoftoworks.normativecontrol.api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.maeasoftoworks.normativecontrol.api.integrations.s3.S3;
import ru.maeasoftoworks.normativecontrol.api.mq.DocumentMessageBody;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.isVerified.IsVerifiedRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.isVerified.IsVerifiedResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.verification.VerificationRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.verification.VerificationResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.verifiedDocument.VerifiedDocumentRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.documents.verifiedDocument.VerifiedDocumentResponse;
import ru.maeasoftoworks.normativecontrol.api.utils.CorrelationIdUtils;

import java.io.*;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final MqPublisher mqPublisher;
    private final S3 s3;

    // Доступен всем
    @PostMapping("/verification")
    public ResponseEntity<String> sendToVerification(@Valid VerificationRequest verificationRequest) throws IOException {
        DocumentMessageBody resultFileName = uploadFile(verificationRequest.getDocument().getInputStream());
        if (resultFileName == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Uploading of file has failed");
        }
        mqPublisher.publishToVerify(resultFileName.getAsJsonString(), resultFileName.getCorrelationId());
        VerificationResponse response = new VerificationResponse(resultFileName.getCorrelationId());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getAsJsonString());
    }

    //TODO: Сделать систему привязки работ к анонимным пользователям

    // Доступен всем, но выдаёт ответ только если запрошенная работа доступна тому кто запрашивает.
    // Нормоконтроллер и админ имеют доступ ко всем работам, остальные - только к своей
    @GetMapping("/isVerified")
    public ResponseEntity<String> isDocumentVerified(@Valid IsVerifiedRequest isVerifiedRequest) {
        String target1 = isVerifiedRequest.getDocumentId() + "/result.docx";
        String target2 = isVerifiedRequest.getDocumentId() + "/result.html";
        boolean result = s3.objectExists(target1) && s3.objectExists(target2);

        if (result) {
            IsVerifiedResponse isVerifiedResponse = new IsVerifiedResponse("Document with id " + isVerifiedRequest.getDocumentId() + " is verified");

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(isVerifiedResponse.getAsJsonString());
        } else {
            IsVerifiedResponse isVerifiedResponse = new IsVerifiedResponse("Document with id " + isVerifiedRequest.getDocumentId() + " is not verified");
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), isVerifiedResponse.getMessage());
        }
    }

    // Доступен всем, но выдаёт ответ только если запрошенная работа доступна тому кто запрашивает.
    // Нормоконтроллер и админ имеют доступ ко всем работам, остальные - только к своей
    @GetMapping("/verifiedDocument")
    @SneakyThrows
    public ResponseEntity<byte[]> getVerifiedDocument(@Valid VerifiedDocumentRequest verifiedDocumentRequest) {
        try (ByteArrayOutputStream result = s3.getObject(verifiedDocumentRequest.getDocumentId() + "/result." + verifiedDocumentRequest.getDocumentType())) {
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

    // TODO: Controller: list, выдаёт данные о работе, которую проверял юзер

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
            DocumentMessageBody documentMessageBody = new DocumentMessageBody(correlationId, fileName);
            s3.putObject(inputStream, documentMessageBody.getDocument());
            return documentMessageBody;
        } catch (Exception e) {
            log.warn("Error occurred: " + e);
            return null;
        }
    }
}
