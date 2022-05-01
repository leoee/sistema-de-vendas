package com.puc.sistemasdevendas.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPatch {
    private String name;
    private String cpf;
    @Size( min = 7, message = "Password must have at least 7 chars")
    private String password;
    private String cellphoneNumber;
    private AddressPatch address;
}

