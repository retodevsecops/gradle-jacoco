package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class OcrDataUtil {

    public static Optional<OcrDataVO> getByName(List<OcrDataVO> ocrData, String name) {
        return ocrData.stream()
                .filter(data -> data.getName().equalsIgnoreCase(name))
                .findFirst();
    }

}
