package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PatchOrderRequest {
    @NotNull
    private OrderStatus orderStatus;
}
