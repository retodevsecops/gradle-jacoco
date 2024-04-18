package com.consubanco.gcsstorage.commons;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.unit.DataSize;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;

import static com.consubanco.model.entities.agreement.message.AgreementTechnicalMessage.FAIL_GET_CONFIG_LOCAL;

@UtilityClass
public class FileUtil {

    public String getSize(BlobInfo blobInfo) {
        DataSize dataSize = DataSize.ofBytes(blobInfo.getSize());
        if (dataSize.toMegabytes() >= 1) {
            return String.format("%s MB", dataSize.toMegabytes());
        }
        return String.format("%s KB", dataSize.toKilobytes());
    }

    public String getFileName(String documentPath) {
        String[] parts = documentPath.split("/");
        return parts[parts.length - 1];
    }

    public byte[] getContentFromResource(ClassPathResource resource) {
        try {
            return FileCopyUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException exception) {
            throw new RuntimeException("Error getting resource content.", exception);
        }
    }

    public String getDirectory(String documentPath) {
        int lastSlashIndex = documentPath.lastIndexOf('/');
        if (lastSlashIndex != -1) return documentPath.substring(0, lastSlashIndex);
        return "/";
    }

    public Mono<BlobInfo> buildBlob(String bucketName, String nameFile) {
        return Mono.just(BlobId.of(bucketName, nameFile))
                .map(blobId -> Blob.newBuilder(blobId).build());
    }

    public Mono<byte[]> base64ToBytes(String contentFileBase64) {
        return Mono.just(contentFileBase64)
                .map(contentFile -> Base64.getDecoder().decode(contentFile));
    }

    public static String decodeBase64(String base64) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        return new String(decodedBytes);
    }



}
