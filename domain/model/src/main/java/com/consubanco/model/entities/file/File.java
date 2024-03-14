package com.consubanco.model.entities.file;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class File {
    private String name;
    private String content;
    private String url;
    private String bucketName;
}
