package vn.edu.uit.socialjob.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.PublicAccessType;

@Configuration
@ConditionalOnProperty(prefix = "azure.storage", name = "enabled", havingValue = "true")
public class AzureBlobStorageConfig {

    @Bean
    public BlobServiceClient blobServiceClient(AzureBlobStorageProperties properties) {
        if (properties.getConnectionString() == null || properties.getConnectionString().isBlank()) {
            throw new IllegalStateException("azure.storage.connection-string is required");
        }

        return new BlobServiceClientBuilder()
            .connectionString(properties.getConnectionString())
            .buildClient();
    }

    @Bean
    public BlobContainerClient blobContainerClient(
        BlobServiceClient blobServiceClient,
        AzureBlobStorageProperties properties
    ) {
        if (properties.getContainerName() == null || properties.getContainerName().isBlank()) {
            throw new IllegalStateException("azure.storage.container-name is required");
        }

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(properties.getContainerName());
        containerClient.createIfNotExists();
        containerClient.setAccessPolicy(PublicAccessType.BLOB, null);
        return containerClient;
    }
}
