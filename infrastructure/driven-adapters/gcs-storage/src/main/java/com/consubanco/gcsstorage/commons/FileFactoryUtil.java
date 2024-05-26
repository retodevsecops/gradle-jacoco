package com.consubanco.gcsstorage.commons;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.vo.FileWithStorageRouteVO;
import com.google.cloud.storage.Blob;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

@UtilityClass
public class FileFactoryUtil {

    private static final String URI_GS = "gs://%s/%s";

    public static File buildFromBlob(Blob blob) {
        return File.builder()
                .name(FileUtil.getFileName(blob.getName()))
                .content(Base64.encodeBase64String(blob.getContent()))
                .directoryPath(FileUtil.getDirectory(blob.getName()))
                .size(FileUtil.getSize(blob))
                .storageRoute(String.format(URI_GS, blob.getBucket(), blob.getName()))
                .build();
    }

    public static File buildFromBlobWithUrl(Blob blob, String url) {
        return File.builder()
                .url(url)
                .name(FileUtil.getFileName(blob.getName()))
                .content(Base64.encodeBase64String(blob.getContent()))
                .directoryPath(FileUtil.getDirectory(blob.getName()))
                .size(FileUtil.getSize(blob))
                .storageRoute(String.format(URI_GS, blob.getBucket(), blob.getName()))
                .build();
    }

    public static FileWithStorageRouteVO buildFileWithStorageRouteVO(Blob blob) {
        return FileWithStorageRouteVO.builder()
                .name(FileUtil.getFileName(blob.getName()))
                .size(FileUtil.getSize(blob))
                .storageRoute(String.format(URI_GS, blob.getBucket(), blob.getName()))
                .build();
    }

    public static File completeFileFromBlob(File file, Blob blob) {
        return file.toBuilder()
                .url(blob.getSelfLink())
                .size(FileUtil.getSize(blob))
                .storageRoute(String.format(URI_GS, blob.getBucket(), blob.getName()))
                .build();
    }

}
