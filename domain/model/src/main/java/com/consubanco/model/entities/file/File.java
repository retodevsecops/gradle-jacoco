package com.consubanco.model.entities.file;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class File {

    private String name;
    private String content;
    private String url;
    private String directoryPath;
    private String size;
    private String extension;

    public File(String content, String extension) {
        this.content = content;
        this.extension = extension;
    }

    public String fullPath() {
        String directory = this.directoryPath;
        if (!directory.endsWith("/")) directory += "/";
        return directory.concat(this.name);
    }

}
