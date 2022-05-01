package com.puc.sistemasdevendas.model.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Document(collection = "Item")
public class Item {
    @Id
    @Null(message = "Can not set id")
    private String id;
    @NotNull(message = "stock quantity can not be null")
    private Integer stockQuantity;
    @NotNull(message = "price quantity can not be null")
    private Double price;
    @NotEmpty(message = "description quantity can not be empty")
    private String description;
    @NotEmpty(message = "name quantity can not be empty")
    private String name;
    @NotEmpty(message = "imageUrl quantity can not be empty")
    private String imageUrl;
    @NotEmpty(message = "specification quantity can not be empty")
    private String specification;
}
