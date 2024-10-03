package com.consubanco.model.entities.file.util;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.file.constant.FileExtensions;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileFactoryUtil {

    public static File buildPDF(String name, String content, String directory) {
        return File.builder()
                .name(name)
                .content(content)
                .directoryPath(directory)
                .extension(FileExtensions.PDF)
                .build();
    }

    public static File buildFromFileUploadVO(FileUploadVO fileUploadVO, String directory) {
        return File.builder()
                .name(fileUploadVO.getName())
                .content(fileUploadVO.getContent())
                .directoryPath(directory)
                .extension(fileUploadVO.getExtension())
                .build();
    }

}
