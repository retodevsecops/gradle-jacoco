package com.consubanco.model.entities.document;

import com.consubanco.model.entities.field.Field;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Document {
    private String id;
    private String name;
    private String technicalName;
    private String order;
    private String classification;
    private Boolean isRequired;
    private Boolean isVisible;
    private Boolean isSpecial;
    private String type;
    private String max;
    private List<String> typeFile;
    private Boolean isClient;
    private String convertTo;
    private List<Field> fields;
}
