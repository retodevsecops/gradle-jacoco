package com.consubanco.model.entities.document;

import com.consubanco.model.entities.agreement.Agreement;
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
    private String classification;
    private Boolean isRequired;
    private Boolean isVisible;
    private List<Field> fields;
}
