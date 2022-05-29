package com.puc.sistemasdevendas.model.entities;

public enum OrderStatus {
    OPEN,
    WAITING_PAYMENT,
    PAYMENT_CONFIRMED,
    IN_TRANSIT,
    PAYMENT_CAPTURED,
    DELIVERED,
    CANCELLED,
    ERROR;
}
