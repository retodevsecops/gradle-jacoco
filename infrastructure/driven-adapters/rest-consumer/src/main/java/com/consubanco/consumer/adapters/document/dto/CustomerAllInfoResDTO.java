package com.consubanco.consumer.adapters.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAllInfoResDTO {

    private CustomerData customer;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerData {
        private String firstName;
        private String secondName;
        private String lastName;
        private String secondLastName;
        private String rfc;
        private String email;
    }

    public String getNames() {
        return customer.firstName + " " + customer.secondName;
    }

}
