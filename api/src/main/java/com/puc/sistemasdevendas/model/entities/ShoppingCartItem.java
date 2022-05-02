package com.puc.sistemasdevendas.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ShoppingCartItem {
    private String name;
    private Integer amount;
    private String itemId;
    @JsonIgnore
    private Item item;
}
