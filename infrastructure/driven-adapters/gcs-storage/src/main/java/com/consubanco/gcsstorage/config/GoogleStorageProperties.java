package com.consubanco.gcsstorage.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "adapter.gcs-storage")
public class GoogleStorageProperties {

    private Double maxFileSizeMB;
    private String bucketName;
    private String publicUrl;
    private int signUrlDays;
    private FilesPath filesPath;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilesPath {
        private String payloadTemplate;
        private String agreementsConfig;
        private String createApplicationTemplate;

    }

    public String agreementsConfigPath() {
        return this.getFilesPath().getAgreementsConfig();
    }

    public String payloadTemplatePath() {
        return this.getFilesPath().getPayloadTemplate();
    }

}
