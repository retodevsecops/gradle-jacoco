package com.consubanco.model.entities.agreement;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/*
* technical exception 500
* business exception 409
* handler exception log de error
* copiar helper de log:
* log
* variable de autenticacion del api va por variable de entorno
*
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Agreement {

    private String id;
    private String number;
    private String name;
    private String businessName;
    private List<Document> documents;
    private List<Annexe>  annexes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Document {
        private String id;
        private String name;
        private String technicalName;
        private String classification;
        private Boolean required;
        private Boolean visible;
        private List<Field> fields;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Field {

        private String id;
        private Integer order;
        private String name;
        private String technicalName;
        private String classification;
        private String type;
        private Boolean required;
        private String max;
        private Boolean isSpecial;
        private String convertTo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Annexe {
        private String id;
        private String order;
        private String name;
        private String technicalName;
        private String classification;
        private String type;
        private Boolean required;
        private String max;
        private Boolean isSpecial;
        private List<String> typeFile;
        private Boolean isClient;
        private String convertTo;
    }

}
