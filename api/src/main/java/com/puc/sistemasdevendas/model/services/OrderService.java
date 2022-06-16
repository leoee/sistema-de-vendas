package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.*;
import com.puc.sistemasdevendas.model.exceptions.BadRequestException;
import com.puc.sistemasdevendas.model.exceptions.ForbidenException;
import com.puc.sistemasdevendas.model.exceptions.NotFoundException;
import com.puc.sistemasdevendas.model.helpers.DecodeToken;
import com.puc.sistemasdevendas.model.repositories.ItemRepository;
import com.puc.sistemasdevendas.model.repositories.OrderRepository;
import com.puc.sistemasdevendas.model.repositories.UserRepository;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.puc.sistemasdevendas.model.constants.ApiConstans.ADM_ROLE;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DecodeToken decodeToken;
    @Autowired
    private MongoTemplate mongoTemplate;
    private final Logger logger = Logger.getLogger(ShoppingCartService.class);

    public void createOrder(User owner, ShoppingCart shoppingCart,
                            CheckoutShoppingCartRequest checkoutShoppingCartRequest) {
        Order createdOrder = this.createBasicOrder(owner, shoppingCart, checkoutShoppingCartRequest);

        this.updateOrderStatus(createdOrder, createdOrder.getOrderStatus().toString(),
                OrderStatus.WAITING_PAYMENT.toString(), "internal");

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

    private void updateOrderStatus(Order order, String previousStatus, String nextStatus, String origin) {
        if (nextStatus.equals(previousStatus)) {
            throw new BadRequestException("Current order status is already the mentioned status.");
        }

        order.setChangeLogs(this.updateChangeLog(order.getChangeLogs(), previousStatus, nextStatus, origin));
        order.setOrderStatus(OrderStatus.valueOf(nextStatus));
    }

    public void confirmPayment(String token, String orderId) {
        User fetchedUser = this.getUserFromToken(token);

        if (!ADM_ROLE.equals(fetchedUser.getRole())) {
            this.logger.warn("An attempt to update payment was made using user " + fetchedUser.getEmail());
            throw new ForbidenException("Could not update payment due invalid permissions");
        }

        Order fetchedOrder = this.orderRepository.findById(orderId).orElse(null);

        if (fetchedOrder == null) {
            throw new NotFoundException("Could not found orderId: " + orderId);
        }

        this.updateOrderStatus(fetchedOrder, fetchedOrder.getOrderStatus().toString(),
                OrderStatus.PAYMENT_CONFIRMED.toString(), "external");

        fetchedOrder.getInvoice().setPaymentStatus(PaymentStatus.PAYMENT_CAPTURED);
        this.orderRepository.save(fetchedOrder);

    }

    private User getUserFromToken(String token) {
        String emailFromToken = this.decodeToken.getGetFromToken(token);

        User fetchedUser = this.userRepository.findUserByEmail(emailFromToken).orElse(null);
        if (fetchedUser == null) {
            this.logger.error("Could not get user by email from token: " + emailFromToken);
            throw new ForbidenException("Could not get user from token");
        }

        return fetchedUser;
    }

    public List<Order> getOrders(String token, String id, String ownerEmail, String orderStatus, String paymentStatus) {
        User fetchedUser = this.getUserFromToken(token);
        Query query = new Query();

        if (!ADM_ROLE.equals(fetchedUser.getRole())) {
            this.logger.warn("An attempt to update payment was made using user " + fetchedUser.getEmail());
            throw new ForbidenException("Could not update payment due invalid permissions");
        }

        if (id != null) {
            query.addCriteria(where("id").is(id));
        }

        if (ownerEmail != null) {
            User orderOwner = this.userRepository.findUserByEmail(ownerEmail).orElse(null);

            if (orderOwner == null) {
                throw new NotFoundException("Not found user with e-mail: " + ownerEmail);
            }
            query.addCriteria(where("ownerId").is(orderOwner.getId()));
        }

        if (orderStatus != null) {
            query.addCriteria(where("orderStatus").is(orderStatus));
        }

        if (paymentStatus != null) {
            query.addCriteria(where("invoice.paymentStatus").is(paymentStatus));
        }

        return this.mongoTemplate.find(query, Order.class);
    }

    public List<Order> getAllMineOrders(String token) {
        User fetchedUser = this.getUserFromToken(token);
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));
        query.addCriteria(where("ownerId").is(fetchedUser.getId()));
        return this.mongoTemplate.find(query, Order.class);
    }

    public void updateOrderStatus(String token, String orderId, PatchOrderRequest patchOrderRequest) {
        User fetchedUser = this.getUserFromToken(token);

        if (!ADM_ROLE.equals(fetchedUser.getRole())) {
            this.logger.warn("An attempt to update payment was made using user " + fetchedUser.getEmail());
            throw new ForbidenException("Could not update payment due invalid permissions");
        }

        Order fetchedOrder = this.orderRepository.findById(orderId).orElse(null);

        if (fetchedOrder == null) {
            throw new NotFoundException("Could not found orderId: " + orderId);
        }

        this.updateOrderStatus(fetchedOrder, fetchedOrder.getOrderStatus().toString(),
                patchOrderRequest.getOrderStatus().toString(), "external");

        this.orderRepository.save(fetchedOrder);

    }
}
