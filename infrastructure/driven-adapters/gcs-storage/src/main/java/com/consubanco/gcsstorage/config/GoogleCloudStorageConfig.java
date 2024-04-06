package com.consubanco.gcsstorage.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GoogleCloudStorageConfig {

    private final GoogleStorageProperties storageProperties;


    @Bean
    public Storage getStorage() {
        return StorageOptions.newBuilder()
                .setProjectId(storageProperties.getProjectId())
                .build()
                .getService();
    }

}
