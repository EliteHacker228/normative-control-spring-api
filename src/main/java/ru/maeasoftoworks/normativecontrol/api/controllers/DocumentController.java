package ru.maeasoftoworks.normativecontrol.api.controllers;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.maeasoftoworks.normativecontrol.api.integrations.s3.S3;
import ru.maeasoftoworks.normativecontrol.api.mq.DocumentMessageBody;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;
import ru.maeasoftoworks.normativecontrol.api.utils.CorrelationIdUtils;

import java.io.*;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final MqPublisher mqPublisher;
    private final S3 s3;

    @PostMapping("/verification")
    public ResponseEntity<String> sendToVerification(@RequestParam(name = "document") MultipartFile file) throws IOException {
        DocumentMessageBody resultFileName = uploadFile(file.getInputStream());
        if (resultFileName == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500));
        }
        mqPublisher.publishToVerify(resultFileName.getAsJsonString(), resultFileName.getCorrelationId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("documentId", resultFileName.getCorrelationId());
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonObject.toJSONString());
    }

    @GetMapping("/isVerified")
    public ResponseEntity<String> isDocumentVerified(@RequestParam String documentId) {
        String target1 = documentId + "/result.docx";
        String target2 = documentId + "/result.html";
        boolean result = s3.objectExists(target1) && s3.objectExists(target2);
        JSONObject jsonObject = new JSONObject();

        if (result) {
            jsonObject.put("message", "document with id " + documentId + " is verified");

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());  // todo create dto
        } else {
            jsonObject.put("message", "document with id " + documentId + " is not verified");

            return ResponseEntity // todo throw ResponseStatusException
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());
        }
    }


    @GetMapping("/verifiedDocument")
    @SneakyThrows
    public ResponseEntity<byte[]> isDocumentVerified(@RequestParam String documentId, @RequestParam String documentType) {
        try (ByteArrayOutputStream result = s3.getObject(documentId + "/result." + documentType)) {
            if (result != null) {
                val bytes = result.toByteArray(); // todo BLOCKING
                if (documentType.equals("docx")) {
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(bytes);
                }
                if (documentType.equals("html")) {
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.TEXT_HTML)
                            .body(bytes);
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Result of type " + documentType + " is not exists");
        }
    }

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
