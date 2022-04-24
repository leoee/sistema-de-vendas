package com.puc.sistemasdevendas.model.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GlobalError {
    private Integer status;
    private String msg;
}
