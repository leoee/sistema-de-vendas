package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PatchShoppingCartItem {
    @Min(1)
    private int amount;
}
