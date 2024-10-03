package com.consubanco.api.services.file.dto;

import com.consubanco.model.entities.file.File;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FileResDTO {

    @Schema(description = "Unique identifier of the file in the storage.", example = "rfq34rtqerg", requiredMode = REQUIRED)
    private String id;

    @Schema(description = "File name as found in the directory.", example = "document-name", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "Public Url to view the file.", example = "https://storage.googleapis.com/csb-venta-digital/renewal/offer/123/document", requiredMode = REQUIRED)
    private String url;

    @Schema(description = "Directory where the file is saved.", example = "renewal/offer/123/", requiredMode = REQUIRED)
    private String directoryPath;

    @Schema(description = "Size of the file.", example = "gs://csb-venta-digital/renewal/offer/123/documents/document", requiredMode = REQUIRED)
    private String storageRoute;

    @Schema(description = "Size of the file.", example = "63 KB", requiredMode = REQUIRED)
    private String size;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Creation date of the file.", example = "yyyy-MM-dd HH:mm:ss", requiredMode = REQUIRED)
    private LocalDateTime creationDate;

    @Schema(description = "Metadata of the file.", requiredMode = NOT_REQUIRED)
    private Map<String, String> metadata;

    public FileResDTO(File file) {
        this.id = file.getId();
        this.url = file.getUrl();
        this.directoryPath = file.getDirectoryPath();
        this.size = file.getSize();
        this.name = file.getName();
        this.storageRoute = file.getStorageRoute();
        this.creationDate = file.getCreationDate();
        this.metadata = file.getMetadata();
    }

}
