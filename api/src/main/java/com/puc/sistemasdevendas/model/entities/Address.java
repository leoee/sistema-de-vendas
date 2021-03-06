package com.puc.sistemasdevendas.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    @NotEmpty
    private String zip;
    @NotEmpty
    private String place;
    @NotEmpty
    private String city;
}
