package com.consubanco.model.entities.file;

import lombok.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Objects;

import static com.consubanco.model.commons.exception.factory.ExceptionFactory.buildBusiness;
import static com.consubanco.model.entities.file.message.FileBusinessMessage.INCOMPLETE_DATA;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class File {

    private String id;
    private String name;
    private String content;
    private String url;
    private String directoryPath;
    private String size;
    private String extension;
    private String storageRoute;
    private LocalDateTime creationDate;

    public File(String content, String extension) {
        this.content = content;
        this.extension = extension;
    }

    public String fullPath() {
        String directory = this.directoryPath;
        if (!directory.endsWith("/")) directory += "/";
        return directory.concat(this.name);
    }

    public byte[] contentDecode() {
        return Base64.getDecoder().decode(this.content);
    }

    public Mono<File> checkRequiredData() {
        if (Objects.isNull(name) || Objects.isNull(content)) return buildBusiness(INCOMPLETE_DATA);
        return Mono.just(this);
    }

    public boolean checkCreationDays(Integer days) {
        long daysBetween = ChronoUnit.DAYS.between(creationDate, LocalDateTime.now());
        return daysBetween <= days;
    }

    public boolean checkCreationMinutes(Integer minutes) {
        long minutesBetween = ChronoUnit.MINUTES.between(creationDate, LocalDateTime.now());
        return minutesBetween <= minutes;
    }

}
