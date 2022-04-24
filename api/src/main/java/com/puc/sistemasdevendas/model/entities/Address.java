package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Address {
    @NotEmpty
    private String zip;
    @NotEmpty
    private String place;
    @NotEmpty
    private String city;
}
