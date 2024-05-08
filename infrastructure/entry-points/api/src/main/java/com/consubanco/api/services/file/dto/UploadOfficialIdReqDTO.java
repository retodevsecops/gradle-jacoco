package com.consubanco.api.services.file.dto;

import com.consubanco.api.commons.util.FilePartUtil;
import com.consubanco.model.entities.file.vo.FileUploadVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UploadOfficialIdReqDTO {

    @Schema(description = "File content in base64.", requiredMode = REQUIRED)
    private String fileContentInBase64;

    @Schema(description = "File extension.", example = "JPEG", requiredMode = REQUIRED)
    private String fileExtension;

    public FileUploadVO toEntity(){
        return FileUploadVO.builder()
                .content(fileContentInBase64)
                .extension(fileExtension)
                .sizeInMB(FilePartUtil.getSizeFileInMBFromBase64(fileContentInBase64))
                .build();
    }

}
