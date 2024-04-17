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

    private String projectId;
    private String bucketName;
    private String publicUrl;
    private int signUrlDays;
    private FilesPath filesPath;

    public String getPublicUrl(String blobName) {
        return String.format(this.publicUrl, this.getBucketName(), blobName);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilesPath {
        private String payloadTemplate;
        private String agreementsConfig;
    }

    public String payloadTemplatePath(){
        return this.getFilesPath().getPayloadTemplate();
    }

}
