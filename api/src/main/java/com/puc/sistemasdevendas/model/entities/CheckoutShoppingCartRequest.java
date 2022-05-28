package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

@Data
public class CheckoutShoppingCartRequest {
    private String notesForDelivery;
    private PaymentMethods paymentMethod;
}
