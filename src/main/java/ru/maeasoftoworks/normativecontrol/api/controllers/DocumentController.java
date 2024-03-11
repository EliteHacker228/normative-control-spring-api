package ru.maeasoftoworks.normativecontrol.api.controllers;

import io.minio.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.maeasoftoworks.normativecontrol.api.mq.DocumentMessageBody;
import ru.maeasoftoworks.normativecontrol.api.mq.MqPublisher;
import ru.maeasoftoworks.normativecontrol.api.utils.CorrelationIdUtils;

import java.io.*;

@RestController
@RequestMapping("/documents")
@AllArgsConstructor
public class DocumentController {

    private MqPublisher mqPublisher;

    @PostMapping("/verification")
    public ResponseEntity<String> sendToVerification(@RequestParam(name = "document", required = true) MultipartFile file) throws IOException {
        DocumentMessageBody resultFileName = s3UploadFile(file.getInputStream());
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
        boolean result = s3isDocumentVerificationExists(documentId);
        JSONObject jsonObject = new JSONObject();

        if (result) {
            jsonObject.put("message", "document with id " + documentId + " is verified");

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());
        } else {
            jsonObject.put("message", "document with id " + documentId + " is not verified");

            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());
        }
    }

    @GetMapping("/verifiedDocument")
    @SneakyThrows
    public ResponseEntity<byte[]> isDocumentVerified(@RequestParam String documentId, @RequestParam String documentType) {
        InputStream result = s3getVerificationResult(documentId, documentType);

        if (result != null) {

            if (documentType.equals("docx"))
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(result.readAllBytes());
            else if(documentType.equals("html"))
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(result.readAllBytes());
            else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", "Result of type " + documentType + " is not exists");

                return ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonObject.toJSONString().getBytes("UTF-8"));
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Result with of type " + documentType + " is not exists");

            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString().getBytes("UTF-8"));
        }
    }

    private DocumentMessageBody s3UploadFile(InputStream inputStream) {
        try {
            final String BUCKET_NAME = "normative-control";
            final String BUCKET_ENDPOINT = "http://localhost:9000";
            final String BUCKET_ACCESS_KEY = "pdh5niPCbqYxGW5BKxGv";
            final String BUCKET_SECRET_KEY = "xqtc6gXpKy0OIZVVSeinxDd7dd0pbDsOEtU7huJX";

            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(BUCKET_ENDPOINT)
                            .region("US-EAST-1")
                            .credentials(BUCKET_ACCESS_KEY, BUCKET_SECRET_KEY)
                            .build();

            // Make 'normative-control' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).region("US-EAST-1").build());
            if (!found) {
                // Make a new bucket called 'normative-control'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).region("US-EAST-1").build());
            } else {
                System.out.println("Bucket 'normative-control' already exists.");
            }

            String fileName = "source.docx";
            String correlationId = CorrelationIdUtils.generateCorrelationId();
            DocumentMessageBody documentMessageBody = new DocumentMessageBody(correlationId, fileName);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME).region("US-EAST-1")
                            .object(documentMessageBody.getDocument())
                            .stream(inputStream, inputStream.available(), -1)
                            .build());

            return documentMessageBody;
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
        return null;
    }

    private boolean s3isDocumentVerificationExists(String documentId) {
        try {
            final String BUCKET_NAME = "normative-control";
            final String BUCKET_ENDPOINT = "http://localhost:9000";
            final String BUCKET_ACCESS_KEY = "pdh5niPCbqYxGW5BKxGv";
            final String BUCKET_SECRET_KEY = "xqtc6gXpKy0OIZVVSeinxDd7dd0pbDsOEtU7huJX";

            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(BUCKET_ENDPOINT)
                            .region("US-EAST-1")
                            .credentials(BUCKET_ACCESS_KEY, BUCKET_SECRET_KEY)
                            .build();

            // Make 'normative-control' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).region("US-EAST-1").build());
            if (!found) {
                // Make a new bucket called 'normative-control'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).region("US-EAST-1").build());
            } else {
                System.out.println("Bucket 'normative-control' already exists.");
            }

            String target1 = documentId + "/result.docx";
            String target2 = documentId + "/result.html";

            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(BUCKET_NAME).region("US-EAST-1")
                    .object(target1)
                    .build());

            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(BUCKET_NAME).region("US-EAST-1")
                    .object(target2)
                    .build());

            return true;
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
            return false;
        }
    }

    private InputStream s3getVerificationResult(String documentId, String documentType) {
        try {
            final String BUCKET_NAME = "normative-control";
            final String BUCKET_ENDPOINT = "http://localhost:9000";
            final String BUCKET_ACCESS_KEY = "pdh5niPCbqYxGW5BKxGv";
            final String BUCKET_SECRET_KEY = "xqtc6gXpKy0OIZVVSeinxDd7dd0pbDsOEtU7huJX";

            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(BUCKET_ENDPOINT)
                            .region("US-EAST-1")
                            .credentials(BUCKET_ACCESS_KEY, BUCKET_SECRET_KEY)
                            .build();

            // Make 'normative-control' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).region("US-EAST-1").build());
            if (!found) {
                // Make a new bucket called 'normative-control'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).region("US-EAST-1").build());
            } else {
                System.out.println("Bucket 'normative-control' already exists.");
            }

            String target = documentId + "/result." + documentType;

            InputStream object = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(BUCKET_NAME).region("US-EAST-1")
                    .object(target)
                    .build());

            return object;
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
            return null;
        }
    }

}
