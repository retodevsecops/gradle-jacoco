package com.consubanco.model.agreement;
import lombok.*;

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
}
