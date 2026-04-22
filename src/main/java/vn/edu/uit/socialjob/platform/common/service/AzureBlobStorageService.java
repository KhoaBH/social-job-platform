package vn.edu.uit.socialjob.platform.common.service;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;

import vn.edu.uit.socialjob.platform.config.AzureBlobStorageProperties;

@Service
public class AzureBlobStorageService {

    private final BlobContainerClient blobContainerClient;
    private final AzureBlobStorageProperties properties;

    public AzureBlobStorageService(
        BlobContainerClient blobContainerClient,
        AzureBlobStorageProperties properties
    ) {
        this.blobContainerClient = blobContainerClient;
        this.properties = properties;
    }

    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        // Step 2: Build a safe blob name so every upload has a unique path.
        String normalizedFolder = normalizeFolder(folder);
        String blobName = buildBlobName(normalizedFolder, file);

        // Step 3: Resolve a blob client and stream the file directly to Azure.
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        try {
            blobClient.upload(file.getInputStream(), file.getSize(), true);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read upload file", ex);
        }

        // Step 4: Apply content type metadata when the client sends one.
        if (file.getContentType() != null && !file.getContentType().isBlank()) {
            blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));
        }

        // Step 5: Return a public URL so the caller can store it in DB.
        return buildPublicUrl(blobName);
    }

    /**
     * Step 1: Convert a public blob URL back to a blob name.
     * This lets us delete old files when a new file replaces them.
     */
    public void deleteByUrl(String blobUrl) {
        if (blobUrl == null || blobUrl.isBlank()) {
            return;
        }

        String blobName = extractBlobName(blobUrl);
        if (blobName.isBlank()) {
            return;
        }

        // Step 2: Delete only when the blob actually exists.
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        if (blobClient.exists()) {
            blobClient.delete();
        }
    }

    /**
     * Step 1: Build the final public URL that will be stored in the database.
     * We prefer an explicit base URL if one is configured, otherwise fall back to Azure's container URL.
     */
    public String buildPublicUrl(String blobName) {
        if (properties.getPublicBaseUrl() != null && !properties.getPublicBaseUrl().isBlank()) {
            return trimTrailingSlash(properties.getPublicBaseUrl()) + "/" + blobName;
        }

        return trimTrailingSlash(blobContainerClient.getBlobContainerUrl()) + "/" + blobName;
    }

    /**
     * Step 1: Normalize folder names so caller code can pass values like "avatars" or "/avatars/" safely.
     */
    private String normalizeFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return "uploads";
        }

        return folder.trim().replaceAll("^/+|/+$", "");
    }

    /**
     * Step 1: Generate a blob name using folder + UUID + file extension.
     * This keeps names unique and still human-readable enough for debugging.
     */
    private String buildBlobName(String folder, MultipartFile file) {
        String extension = resolveExtension(file);
        return folder + "/" + UUID.randomUUID() + extension;
    }

    /**
     * Step 1: Try to keep the original file extension.
     * Step 2: If the name has no extension, fall back to the MIME type when possible.
     */
    private String resolveExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            String extension = originalName.substring(originalName.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            return extension;
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return "";
        }

        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "application/pdf" -> ".pdf";
            default -> "";
        };
    }

    /**
     * Step 1: Extract the blob path from a full Azure URL.
     * Step 2: This is used for cleanup when replacing files.
     */
    private String extractBlobName(String blobUrl) {
        String containerUrl = trimTrailingSlash(blobContainerClient.getBlobContainerUrl());
        String publicBaseUrl = properties.getPublicBaseUrl();

        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            String normalizedBaseUrl = trimTrailingSlash(publicBaseUrl);
            if (blobUrl.startsWith(normalizedBaseUrl + "/")) {
                return blobUrl.substring(normalizedBaseUrl.length() + 1);
            }
        }

        if (blobUrl.startsWith(containerUrl + "/")) {
            return blobUrl.substring(containerUrl.length() + 1);
        }

        return "";
    }

    /**
     * Step 1: Remove a trailing slash so URL concatenation stays stable.
     */
    private String trimTrailingSlash(String value) {
        if (value == null) {
            return "";
        }

        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}