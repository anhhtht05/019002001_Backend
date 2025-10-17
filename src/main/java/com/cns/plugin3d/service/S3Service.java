package com.cns.plugin3d.service;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;

@Service
@Getter
public class S3Service {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String firmwarePrefix;
    private final String bucket;
    private final long presignExpiresSeconds;

    public S3Service(
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.s3.firmware-prefix}") String firmwarePrefix,
            @Value("${aws.s3.presign-expires-seconds}") long presignExpiresSeconds) {

        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        this.presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.firmwarePrefix = firmwarePrefix;
        this.bucket = bucket;
        this.presignExpiresSeconds = presignExpiresSeconds;
    }

    public String uploadFile(MultipartFile file, String key) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PRIVATE)
                    .build();

            s3.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
            return getObjectUrl(key);

        } catch (IOException e) {
            throw new RuntimeException("Error reading file bytes", e);
        }
    }

    public boolean exists(String key) {
        try {
            s3.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) return false;
            throw e;
        }
    }


    public String getObjectUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, s3.serviceClientConfiguration().region().id(), key);
    }

    public byte[] getObjectBytes(String key) {
        ResponseBytes<GetObjectResponse> bytes = s3.getObjectAsBytes(
                GetObjectRequest.builder().bucket(bucket).key(key).build());
        return bytes.asByteArray();
    }

    public boolean verifyFileChecksum(String key, String expectedChecksum) {
        try {
            byte[] fileBytes = getObjectBytes(key);
            String actualChecksum = computeSha256(fileBytes);
            return actualChecksum.equalsIgnoreCase(expectedChecksum);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify file checksum", e);
        }
    }

    public String computeSha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to compute SHA-256 hash", ex);
        }
    }

    public String generatePresignedDownload(String key, long expiresMinutes) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest presignRequest =
                software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(expiresMinutes))
                        .getObjectRequest(getObjectRequest)
                        .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }


    @PreDestroy
    public void close() {
        s3.close();
        presigner.close();
    }


//    public PresignedPutObjectRequest createPresignedUpload(String key, String contentType) {
//        PutObjectRequest por = PutObjectRequest.builder()
//                .bucket(bucket)
//                .key(key)
//                .contentType(contentType)
//                .acl(ObjectCannedACL.PRIVATE)
//                .build();
//
//        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
//                .putObjectRequest(por)
//                .signatureDuration(Duration.ofSeconds(presignExpiresSeconds))
//                .build();
//
//        return presigner.presignPutObject(presignRequest);
//    }


}
