package com.puc.sistemasdevendas.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressPatch {
    private String zip;
    private String place;
    private String city;
}
