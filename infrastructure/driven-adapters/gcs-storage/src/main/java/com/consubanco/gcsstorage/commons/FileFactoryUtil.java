package com.consubanco.gcsstorage.commons;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.vo.FileWithStorageRouteVO;
import com.google.cloud.storage.Blob;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@UtilityClass
public class FileFactoryUtil {

    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();
    private static final String URI_GS = "gs://%s/%s";

    public static File buildFromBlob(Blob blob) {
        return buildFileFromBlob(blob, null);
    }

    public static File buildFromBlobWithUrl(Blob blob, String url) {
        return buildFileFromBlob(blob, url);
    }

    public static FileWithStorageRouteVO buildFileWithStorageRouteVO(Blob blob) {
        return FileWithStorageRouteVO.builder()
                .name(FileStorageUtil.getFileName(blob.getName()))
                .size(FileStorageUtil.getSize(blob))
                .storageRoute(String.format(URI_GS, blob.getBucket(), blob.getName()))
                .build();
    }

    public static File completeFileFromBlob(File file, Blob blob) {
        return file.toBuilder()
                .id(blob.getGeneratedId())
                .url(blob.getSelfLink())
                .size(FileStorageUtil.getSize(blob))
                .storageRoute(String.format(URI_GS, blob.getBucket(), blob.getName()))
                .creationDate(blob.getCreateTimeOffsetDateTime().toLocalDateTime())
                .metadata(blob.getMetadata())
                .build();
    }

    private static File buildFileFromBlob(Blob blob, String url) {
        ZonedDateTime zonedDateTime = blob.getCreateTimeOffsetDateTime().atZoneSameInstant(SYSTEM_ZONE_ID);
        return File.builder()
                .id(blob.getGeneratedId())
                .url(url)
                .name(FileStorageUtil.getFileName(blob.getName()))
                .content(Base64.encodeBase64String(blob.getContent()))
                .directoryPath(FileStorageUtil.getDirectory(blob.getName()))
                .size(FileStorageUtil.getSize(blob))
                .storageRoute(String.format(URI_GS, blob.getBucket(), blob.getName()))
                .creationDate(zonedDateTime.toLocalDateTime())
                .metadata(blob.getMetadata())
                .build();
    }

}
