package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserLogin {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Missing email field")
    private String email;
    @NotBlank(message = "Missing password field")
    private String password;
}
