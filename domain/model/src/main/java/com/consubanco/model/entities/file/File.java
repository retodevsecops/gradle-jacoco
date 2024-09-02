package com.consubanco.model.entities.file;

import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class File {

    private static final Pattern BASE_NAME_PATTERN = Pattern.compile("^(.*?)(-\\d+)?$");

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
        return directory.concat(this.name)
                .concat(".")
                .concat(this.extension.toLowerCase());
    }

    public boolean checkCreationDays(Integer days) {
        long daysBetween = ChronoUnit.DAYS.between(creationDate, LocalDateTime.now());
        return daysBetween <= days;
    }

    public boolean checkCreationMinutes(Integer minutes) {
        long minutesBetween = ChronoUnit.MINUTES.between(creationDate, LocalDateTime.now());
        return Math.abs(minutesBetween) <= minutes;
    }

    public String baseFileName() {
        Matcher matcher = BASE_NAME_PATTERN.matcher(name);
        return matcher.matches() ? matcher.group(1) : name;
    }

}
