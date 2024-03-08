package com.consubanco.model.entities.agreement;
import lombok.*;

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
}
