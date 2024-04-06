package com.consubanco.consumer.commons;

import com.consubanco.model.commons.exception.TechnicalException;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

import static com.consubanco.model.entities.file.message.FileTechnicalMessage.READING_ERROR;

@UtilityClass
public class Base64Util {

    public Mono<String> resourceToBase64(Resource resource) {
        return Mono.fromCallable(() -> {
            try {
                byte[] bytes = resource.getContentAsByteArray();
                return Base64.encodeBase64String(bytes);
            } catch (Exception exception) {
                throw new TechnicalException(exception, READING_ERROR);
            }
        });
    }

}
