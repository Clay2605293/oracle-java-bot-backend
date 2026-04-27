package com.oraclejavabot.features.projects.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private final S3Client s3Client;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    @Value("${supabase.storage.public-url-base}")
    private String publicUrlBase;

    public SupabaseStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public UploadedFile uploadProjectDocument(UUID projectId, MultipartFile file) {
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        String safeFileName = sanitizeFileName(originalFileName);
        String timestamp = String.valueOf(Instant.now().toEpochMilli());

        String storagePath = "projects/" + projectId + "/" + timestamp + "-" + safeFileName;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(storagePath)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            String fileUrl = buildPublicUrl(storagePath);

            return new UploadedFile(
                    storagePath,
                    fileUrl,
                    safeFileName,
                    file.getContentType(),
                    file.getSize()
            );

        } catch (IOException e) {
            throw new RuntimeException("Could not read uploaded file", e);
        } catch (Exception e) {
            throw new RuntimeException("Could not upload file to Supabase Storage", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is required");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Uploaded file must have a valid file name");
        }
    }

    private String sanitizeFileName(String fileName) {
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_+", "_");
    }

    private String buildPublicUrl(String storagePath) {
        String cleanBase = publicUrlBase.endsWith("/")
                ? publicUrlBase.substring(0, publicUrlBase.length() - 1)
                : publicUrlBase;

        return cleanBase + "/" + storagePath;
    }

    public static class UploadedFile {
        private final String storagePath;
        private final String fileUrl;
        private final String fileName;
        private final String contentType;
        private final Long fileSizeBytes;

        public UploadedFile(
                String storagePath,
                String fileUrl,
                String fileName,
                String contentType,
                Long fileSizeBytes
        ) {
            this.storagePath = storagePath;
            this.fileUrl = fileUrl;
            this.fileName = fileName;
            this.contentType = contentType;
            this.fileSizeBytes = fileSizeBytes;
        }

        public String getStoragePath() {
            return storagePath;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public Long getFileSizeBytes() {
            return fileSizeBytes;
        }
    }
}