package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

@Data
public class OrderItem {
    private String itemId;
    private String name;
    private Integer amount;
}
