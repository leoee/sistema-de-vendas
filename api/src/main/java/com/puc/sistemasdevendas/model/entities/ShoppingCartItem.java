package com.puc.sistemasdevendas.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCartItem {
    private String name;
    private Integer amount;
    private String itemId;
    private Item item;
}
