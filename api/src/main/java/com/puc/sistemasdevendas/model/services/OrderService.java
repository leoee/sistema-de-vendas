package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.*;
import com.puc.sistemasdevendas.model.repositories.ItemRepository;
import com.puc.sistemasdevendas.model.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;

    public void createOrder(User owner, ShoppingCart shoppingCart,
                            CheckoutShoppingCartRequest checkoutShoppingCartRequest) {
        Order createdOrder = this.createBasicOrder(owner, shoppingCart, checkoutShoppingCartRequest);

        this.updateOrderStatus(createdOrder, OrderStatus.WAITING_PAYMENT.toString(), "internal");

        orderRepository.insert(createdOrder);
    }

    private Order createBasicOrder(User owner, ShoppingCart shoppingCart,
                                   CheckoutShoppingCartRequest checkoutShoppingCartRequest) {
        Order createdOrder = new Order();

        createdOrder.setOwnerId(owner.getId());
        createdOrder.setTotal(shoppingCart.getTotal());
        createdOrder.setOrderStatus(OrderStatus.OPEN);

        Invoice invoice = new Invoice();
        invoice.setPaymentMethod(checkoutShoppingCartRequest.getPaymentMethod());
        invoice.setPaymentStatus(PaymentStatus.WAITING_PAYMENT);

        createdOrder.setInvoice(invoice);

        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setNotes(checkoutShoppingCartRequest.getNotesForDelivery());
        deliveryAddress.setAddress(owner.getAddress());

        createdOrder.setDeliveryAddress(deliveryAddress);
        createdOrder.setCreatedDate(new Date());

        createdOrder.setOrderItems(this.createOrderItems(shoppingCart));
        createdOrder.setChangeLogs(new ArrayList<>());

        return createdOrder;
    }

    public List<OrderItem> createOrderItems(ShoppingCart shoppingCart) {
        List<OrderItem> orderItems = new ArrayList<>();

        shoppingCart.getShoppingCartItemList().forEach(shoppingCartItem -> {
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setItemId(shoppingCartItem.getItemId());
            newOrderItem.setName(shoppingCartItem.getName());
            newOrderItem.setAmount(shoppingCartItem.getAmount());

            this.updateItemStockQuantity(shoppingCartItem);
            orderItems.add(newOrderItem);
        });

        return orderItems;
    }

    public void updateItemStockQuantity(ShoppingCartItem shoppingCartItem) {
        Item fetchedItem = this.itemRepository.findById(shoppingCartItem.getItemId()).orElse(null);

        fetchedItem.setStockQuantity(fetchedItem.getStockQuantity() - shoppingCartItem.getAmount());
        this.itemRepository.save(fetchedItem);
    }

    private List<OrderChangeLog> updateChangeLog(List<OrderChangeLog> changeLogs, String previousStatus, String currentStatus, String origin) {
        OrderChangeLog orderChangeLog = new OrderChangeLog();
        orderChangeLog.setDate(new Date());
        orderChangeLog.setChangeOrigin(origin);
        orderChangeLog.setPreviousStatus(previousStatus);
        orderChangeLog.setCurrentStatus(currentStatus);

        changeLogs.add(orderChangeLog);

        return changeLogs;
    }

    private void updateOrderStatus(Order order, String nextStatus, String origin) {
        order.setChangeLogs(this.updateChangeLog(order.getChangeLogs(),
                OrderStatus.OPEN.toString(), nextStatus, origin));
        order.setOrderStatus(OrderStatus.valueOf(nextStatus));
    }
}
