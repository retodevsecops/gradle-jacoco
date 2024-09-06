package com.consubanco.api.services.ocr.dto;

import com.consubanco.api.services.file.dto.FileResDTO;
import com.consubanco.model.entities.ocr.vo.OcrResulSetVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OcrResulSetResDTO {

    @Schema(description = "File Uploaded in storage.", requiredMode = REQUIRED)
    private FileResDTO file;

    @Schema(description = "Ocr document.", requiredMode = NOT_REQUIRED)
    private OcrDocumentResDTO ocr;


    public OcrResulSetResDTO(OcrResulSetVO resulSetVO) {
        this.file = new FileResDTO(resulSetVO.getFile());
        if (resulSetVO.getOcrDocument() != null) this.ocr = new OcrDocumentResDTO(resulSetVO.getOcrDocument());
    }

}