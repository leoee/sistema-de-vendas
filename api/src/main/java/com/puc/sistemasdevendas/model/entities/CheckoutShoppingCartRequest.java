package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CheckoutShoppingCartRequest {
    private String notesForDelivery;
    @NotNull(message = "A payment method is required.")
    private PaymentMethods paymentMethod;
}
