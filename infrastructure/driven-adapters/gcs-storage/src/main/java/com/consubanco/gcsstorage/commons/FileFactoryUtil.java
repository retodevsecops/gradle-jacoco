package com.consubanco.gcsstorage.commons;

import com.consubanco.model.entities.file.File;
import com.google.cloud.storage.Blob;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileFactoryUtil {

    public static File buildFromBlob(Blob blob) {
        return File.builder()
                .name(FileUtil.getFileName(blob.getName()))
                .content(new String(blob.getContent()))
                .directoryPath(FileUtil.getDirectory(blob.getName()))
                .size(FileUtil.getSize(blob))
                .build();
    }

    public static File buildFromBlobWithUrl(Blob blob, String url) {
        return File.builder()
                .name(FileUtil.getFileName(blob.getName()))
                .url(url)
                .directoryPath(FileUtil.getDirectory(blob.getName()))
                .size(FileUtil.getSize(blob))
                .build();
    }

}
