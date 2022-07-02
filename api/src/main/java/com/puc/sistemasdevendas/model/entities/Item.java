package com.puc.sistemasdevendas.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Document(collection = "Items")
public class Item {
    @Id
    @Null(message = "Can not set id")
    private String id;
    @NotNull(message = "stock quantity can not be null")
    @Min(1)
    private Integer stockQuantity;
    @NotNull(message = "price quantity can not be null")
    @Min(0)
    private Double price;
    @NotEmpty(message = "description quantity can not be empty")
    private String description;
    @NotEmpty(message = "name quantity can not be empty")
    private String name;
    @NotEmpty(message = "imageUrl quantity can not be empty")
    private String imageUrl;
    @NotEmpty(message = "specification quantity can not be empty")
    private String specification;
    private boolean active;
}
