package com.consubanco.model.entities.file.vo;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileStorageVO {
    private String name;
    private String storageRoute;
    private String size;
}
