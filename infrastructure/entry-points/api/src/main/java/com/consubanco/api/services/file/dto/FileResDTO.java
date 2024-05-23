package com.consubanco.api.services.file.dto;

import com.consubanco.model.entities.file.File;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FileResDTO {

    @Schema(description = "File name as found in the directory.", example = "document-name", requiredMode = REQUIRED)
    private String name;

    @Schema(description = "Public Url to view the file.", example = "https://storage.googleapis.com/csb-venta-digital/renewal/offer/123/document", requiredMode = REQUIRED)
    private String url;

    @Schema(description = "Directory where the file is saved.", example = "renewal/offer/123/", requiredMode = REQUIRED)
    private String directoryPath;

    @Schema(description = "Size of the file.",example = "gs://csb-venta-digital/renewal/offer/123/documents/document",  requiredMode = REQUIRED)
    private String storageRoute;

    @Schema(description = "Size of the file.",example = "63 KB",  requiredMode = REQUIRED)
    private String size;

    public FileResDTO(File file) {
        this.url = file.getUrl();
        this.directoryPath = file.getDirectoryPath();
        this.size = file.getSize();
        this.name = file.getName();
        this.storageRoute = file.getStorageRoute();

    }

}
