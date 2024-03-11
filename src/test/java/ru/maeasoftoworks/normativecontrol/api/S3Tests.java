package ru.maeasoftoworks.normativecontrol.api;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class S3Tests {
    @Test
    public void S3Test(){
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

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(BUCKET_NAME).region("US-EAST-1")
                            .object("123456/source.docx")
                            .filename("C:\\Users\\Max\\Desktop\\Api\\normative-control-api\\src\\test\\resources\\sample1(1).docx")
                            .build());
            System.out.println(
                    "'C:\\Users\\Max\\Desktop\\Api\\normative-control-api\\src\\test\\resources\\sample1(1).docx' is successfully uploaded as "
                            + "object '123456/source.docx' to bucket 'normative-control'.");
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
