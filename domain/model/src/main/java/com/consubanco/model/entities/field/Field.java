package com.consubanco.model.entities.field;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Field {
    private String id;
    private Integer order;
    private String name;
    private String technicalName;
    private String classification;
    private String type;
    private Boolean isRequired;
    private String max;
    private Boolean isSpecial;
    private String convertTo;
    private String value;
}
