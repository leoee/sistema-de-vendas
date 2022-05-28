package com.puc.sistemasdevendas.model.entities;

import lombok.Data;

import java.util.Date;

@Data
public class OrderChangeLog {
    private Date date;
    private String previousStatus;
    private String currentStatus;
    private String changeOrigin;
}
