package com.consubanco.model.entities.document.constant;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class DocumentNames {
    public static final String OFFICIAL_ID = "identificacion-oficial";
    public static final String OFFICIAL_ID_FRONT = "identificacion-oficial-anverso";
    public static final String OFFICIAL_ID_BACK = "identificacion-oficial-reverso";

    public static final List<String> PARTS_OFFICIAL_ID = List.of(OFFICIAL_ID_BACK, OFFICIAL_ID_FRONT);
}
