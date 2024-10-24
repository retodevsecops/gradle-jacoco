package com.consubanco.gcsstorage.commons;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.ResourceAccessException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;

@UtilityClass
public class FileStorageUtil {

    public String getSize(BlobInfo blobInfo) {
        DataSize dataSize = DataSize.ofBytes(blobInfo.getSize());
        if (dataSize.toMegabytes() >= 1) {
            return String.format("%s MB", dataSize.toMegabytes());
        }
        return String.format("%s KB", dataSize.toKilobytes());
    }

    public String getFileName(String documentPath) {
        String[] parts = documentPath.split("/");
        String filename = parts[parts.length - 1];
        int lastIndexOfDot = filename.lastIndexOf('.');
        if (lastIndexOfDot == -1) return filename;
        return filename.substring(0, lastIndexOfDot);
    }

    public String getFileNameWithExtension(String documentPath) {
        String[] parts = documentPath.split("/");
        return parts[parts.length - 1];
    }

    public byte[] getContentFromResource(ClassPathResource resource) {
        try {
            return FileCopyUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException exception) {
            throw new ResourceAccessException("Error getting resource content.", exception);
        }
    }

    public Mono<String> getContentInBase64FromResource(ClassPathResource resource) {
        return Mono.just(resource)
                .map(FileStorageUtil::getContentFromResource)
                .map(Base64.getEncoder()::encodeToString);
    }

    public Mono<FileUploadVO> buildFileUploadVOFromResource(ClassPathResource resource) {
        return getContentInBase64FromResource(resource)
                .map(content -> FileUploadVO.builder()
                        .name(resource.getFilename())
                        .content(content)
                        .extension(StringUtils.getFilenameExtension(resource.getFilename()))
                        .sizeInMB(getSizeFromResource(resource))
                        .build());
    }

    public double getSizeFromResource(ClassPathResource resource) {
        try {
            double megabytes = resource.contentLength() / 1048576.0;
            return Math.round(megabytes * 100.0) / 100.0;
        } catch (IOException exception) {
            throw new ResourceAccessException("Error obtaining resource size: ", exception);
        }

    }

    public String getDirectory(String documentPath) {
        int lastSlashIndex = documentPath.lastIndexOf('/');
        if (lastSlashIndex != -1) return documentPath.substring(0, lastSlashIndex);
        return "/";
    }

    public Mono<BlobInfo> buildBlob(String bucketName, String name, String contentType) {
        return Mono.just(BlobId.of(bucketName, name))
                .map(blobId -> BlobInfo.newBuilder(blobId)
                        .setContentType(contentType)
                        .build());
    }

    public Mono<BlobInfo> buildBlobFromFile(File file, String bucketName) {
        String contentType = ContentTypeResolver.getFromFileExtension(file.getExtension());
        return Mono.just(BlobId.of(bucketName, file.fullPath()))
                .map(blobId -> BlobInfo.newBuilder(blobId)
                        .setMetadata(file.getMetadata())
                        .setContentType(contentType)
                        .build());
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
