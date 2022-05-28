package com.puc.sistemasdevendas.model.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "Orders")
@Data
public class Order {
    @Id
    private String id;
    private Double total;
    private OrderStatus orderStatus;
    private String ownerId;
    private DeliveryAddress deliveryAddress;
    private Date createdDate;
    private Invoice invoice;
    private List<OrderItem> orderItems;
    private List<OrderChangeLog> changeLogs;
}
