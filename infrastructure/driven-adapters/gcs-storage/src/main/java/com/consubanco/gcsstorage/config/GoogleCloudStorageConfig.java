package com.consubanco.gcsstorage.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCloudStorageConfig {

    private final String projectId;

    public GoogleCloudStorageConfig(final @Value("${adapter.gcs-storage.project-id}") String projectId) {
        this.projectId = projectId;
    }

    @Bean
    public Storage getStorage() {
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }

}
