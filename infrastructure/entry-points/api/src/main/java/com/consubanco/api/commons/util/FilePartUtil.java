package com.consubanco.api.commons.util;

import com.consubanco.model.entities.file.vo.AttachmentFileVO;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@UtilityClass
public class FilePartUtil {

    public byte[] getBytesFromBuffer(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return bytes;
    }

    public Mono<String> fileToBase64(FilePart filePart) {
        return Mono.just(filePart)
                .map(Part::content)
                .flatMap(DataBufferUtils::join)
                .map(FilePartUtil::getBytesFromBuffer)
                .map(Base64::encodeBase64String);
    }

    public Mono<FileUploadVO> buildFileUploadVOFromFilePart(FilePart filePart) {
        return Mono.zip(FilePartUtil.fileToBase64(filePart), FilePartUtil.getSizeFileInMB(filePart))
                .map(tuple -> FileUploadVO.builder()
                        .name(filePart.name())
                        .extension(StringUtils.getFilenameExtension(filePart.filename()))
                        .content(tuple.getT1())
                        .sizeInMB(tuple.getT2())
                        .build());
    }

    public Mono<FileUploadVO> buildFileUploadVOFromFilePart(FilePart filePart, String fileName) {
        return Mono.zip(FilePartUtil.fileToBase64(filePart), FilePartUtil.getSizeFileInMB(filePart))
                .map(tuple -> FileUploadVO.builder()
                        .name(fileName)
                        .extension(StringUtils.getFilenameExtension(filePart.filename()))
                        .content(tuple.getT1())
                        .sizeInMB(tuple.getT2())
                        .build());
    }

    public Mono<AttachmentFileVO> buildAttachmentFromFilePart(FilePart filePart) {
        return Mono.empty();
    }

    public Mono<Double> getSizeFileInMB(FilePart filePart) {
        return filePart.content()
                .map(buffer -> (long) buffer.readableByteCount())
                .reduce(0L, Long::sum)
                .map(FilePartUtil::bytesToMB)
                .cast(Double.class);
    }

    public double bytesToMB(long bytes) {
        double megabytes = bytes / 1048576.0;
        return Math.round(megabytes * 1000.0) / 1000.0;
    }

    public double getSizeFileInMBFromBase64(String base64EncodedString) {
        byte[] decodedBytes = Base64.decodeBase64(base64EncodedString);
        long fileSizeInBytes = decodedBytes.length;
        return bytesToMB(fileSizeInBytes);
    }

}
