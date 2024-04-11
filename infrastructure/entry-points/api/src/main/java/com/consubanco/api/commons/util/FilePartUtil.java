package com.consubanco.api.commons.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
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

}
