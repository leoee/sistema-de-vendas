package com.puc.sistemasdevendas.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Null;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Document(collection = "Users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @Null(message = "Id can not be set")
    private String id;
    @NotEmpty(message = "Name can not be empty")
    private String name;
    @NotEmpty(message = "email can not be empty")
    @Email(message = "Email format is incorrect")
    private String email;
    @NotEmpty(message = "cpf can not be empty")
    private String cpf;
    @Size( min = 7, message = "Password must have at least 7 chars")
    private String password;
    @Null(message = "role can not be set")
    private String role;
    @NotEmpty(message = "cellphoneNumber can not be empty")
    private String cellphoneNumber;
    private Address address;
}
