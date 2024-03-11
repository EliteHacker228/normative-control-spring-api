package ru.maeasoftoworks.normativecontrol.api.controllers;

import io.minio.*;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/verification")
    public ResponseEntity<String> sendToVerification(@RequestParam(name = "document", required = true) MultipartFile file) throws IOException {
        DocumentMessageBody resultFileName = s3UploadFile(file.getInputStream());
        mqPublisher.publishToVerify(resultFileName.getAsJsonString(), resultFileName.getCorrelationId());
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(resultFileName.getAsJsonString());
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
}
