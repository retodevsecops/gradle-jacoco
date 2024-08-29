package com.consubanco.model.entities.ocr.vo;

import com.consubanco.model.entities.file.File;
import com.consubanco.model.entities.ocr.OcrDocument;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class OcrResulSetVO {
    private File file;
    private OcrDocument ocrDocument;

    public OcrResulSetVO(File file, OcrDocument ocrDocument) {
        this.ocrDocument = ocrDocument;
        this.file = file;
    }

    public OcrResulSetVO(File file) {
        this.file = file;
    }
}
