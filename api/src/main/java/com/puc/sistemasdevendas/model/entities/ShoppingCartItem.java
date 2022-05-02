package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

@Data
public class ShoppingCartItem {
    private String id;
    private String name;
    private Integer amount;
    private String itemId;
    private Item item;
}
