package com.puc.sistemasdevendas.model.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "ShoppingCarts")
public class ShoppingCart {
    @Id
    private String id;
    private Double total;
    private String owner;
    private List<ShoppingCartItem> shoppingCartItemList;
}
