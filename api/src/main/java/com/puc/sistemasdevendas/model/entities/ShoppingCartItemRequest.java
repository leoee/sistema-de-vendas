package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ShoppingCartItemRequest {
    @NotNull
    private String itemId;
    @Min(1)
    private Integer amount;
}
