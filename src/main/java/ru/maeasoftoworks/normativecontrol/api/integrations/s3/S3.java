package ru.maeasoftoworks.normativecontrol.api.integrations.s3;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3 {
    @Value("${s3.bucket}")
    private String bucket;

    private final S3Client s3Client;

    public void putObject(InputStream inputStream, String name) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                .key(name)
                .bucket(bucket)
                .build(),
                RequestBody.fromInputStream(inputStream, inputStream.available())
        );
    }

    public @Nullable ByteArrayOutputStream getObject(String name) {
        try {
            val outputStream = new ByteArrayOutputStream();
            s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(name)
                            .build()
            ).transferTo(outputStream);
            return outputStream;
        } catch (IOException | NoSuchKeyException exception){
            return null;
        }
    }

    public boolean objectExists(String name) {
        val head = s3Client.headObject(
                HeadObjectRequest.builder()
                        .key(name)
                        .bucket(bucket)
                        .build()
        );
        return head.partsCount() > 0;
    }

    @PreDestroy
    private void close() {
        s3Client.close();
    }
}
