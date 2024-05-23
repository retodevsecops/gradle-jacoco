package com.consubanco.model.entities.document.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentNames {
    public static final String OFFICIAL_ID = "identificacion-oficial";
    public static final String APPLICANT_RECORD = "expediente-solicitante";
    private static final String NOM151 = "%s-signed-nom151";

    public String documentNameWithNom151(String documentName){
        return String.format(NOM151, documentName);
    }

}
