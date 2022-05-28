package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

@Data
public class Invoice {
    private PaymentMethods paymentMethod;
    private PaymentStatus paymentStatus;
}
