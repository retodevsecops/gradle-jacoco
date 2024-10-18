package com.consubanco.model.entities.file.util;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import com.consubanco.model.entities.file.message.FileBusinessMessage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtil {

    public static String nameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw ExceptionFactory.business(FileBusinessMessage.NAME_NULL);
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) return fileName.substring(0, dotIndex);
        return fileName;
    }

    public static String extensionFromFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw ExceptionFactory.business(FileBusinessMessage.NAME_NULL);
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        } else {
            throw ExceptionFactory.business(FileBusinessMessage.INVALID_NAME_FORMAT);
        }
    }

    public static double sizeInMBFromBase64(String base64String) {
        long fileSizeInBytes = sizeFromBase64(base64String);
        double sizeInMegabytes = fileSizeInBytes / (1024.0 * 1024.0); // Conversión a megabytes
        return Math.round(sizeInMegabytes * 100.0) / 100.0;
    }

    private static long sizeFromBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            throw new IllegalArgumentException("Base64 string cannot be null or empty");
        }
        base64String = base64String.trim();
        int paddingCount = 0;
        if (base64String.endsWith("==")) {
            paddingCount = 2;
        } else if (base64String.endsWith("=")) {
            paddingCount = 1;
        }
        int base64Length = base64String.length();
        return ((long) base64Length * 3) / 4 - paddingCount;
    }

}
