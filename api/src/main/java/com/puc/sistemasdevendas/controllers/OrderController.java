package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.PatchOrderRequest;
import com.puc.sistemasdevendas.model.services.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@CrossOrigin
@Api(tags = "Operações em pedidos")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "Confirmar pagamento de um pedido")
    @RequestMapping(value = "/orders/{orderId}", method = RequestMethod.POST)
    ResponseEntity<?> confirmPayment(@RequestHeader("Authorization") final String bearerToken,
                              @PathVariable("orderId")
                              @ApiParam(name = "itemId",
                                      value = "Id do pedido a ser confirmado o pagamento",
                                      example = "111233",
                                      required = true)
                                      String orderId) {
        this.orderService.confirmPayment(bearerToken.substring(6), orderId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Buscar todos os pedidos cadastrados no sistema. Somente para administradores")
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") final String bearerToken,
                                   @RequestParam("id")
                                   @ApiParam(name = "id",
                                           value = "Parâmetro para filtrar pedidos a partir de id",
                                           example = "22233322",
                                           required = false)
                                           Optional<String> id,
                                   @RequestParam("ownerId")
                                   @ApiParam(name = "ownerId",
                                           value = "Parâmetro para filtrar pedidos a partir de id do cliente",
                                           example = "99222331",
                                           required = false)
                                           Optional<String> ownerId,
                                   @RequestParam("orderStatus")
                                   @ApiParam(name = "orderStatus",
                                           value = "Parâmetro para filtrar pedidos a partir de seu estado",
                                           example = "OPEN|WAITING_PAYMENT|IN_TRANSIT",
                                           required = false)
                                           Optional<String> orderStatus,
                                   @RequestParam("paymentStatus")
                                   @ApiParam(name = "paymentStatus",
                                           value = "Parâmetro para filtrar pedidos a partir de seu estado",
                                           example = "WAITING_PAYMENT|PAYMENT_CAPTURED",
                                           required = false)
                                           Optional<String> paymentStatus) {
        return ResponseEntity.ok(this.orderService.
                getOrders(bearerToken.substring(6), id.orElse(null), ownerId.orElse(null),
                        orderStatus.orElse(null), paymentStatus.orElse(null)));
    }

    @ApiOperation(value = "Buscar todos os pedidos do usuário do token")
    @RequestMapping(value = "/orders/me", method = RequestMethod.GET)
    ResponseEntity<?> getAllMineOrders(@RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.orderService.
                getAllMineOrders(bearerToken.substring(6)));
    }

    @ApiOperation(value = "Confirmar pagamento de um pedido")
    @RequestMapping(value = "/orders/{orderId}", method = RequestMethod.PATCH)
    ResponseEntity<?> updateOrderStatus(@RequestHeader("Authorization") final String bearerToken,
                                        @PathVariable("orderId")
                                     @ApiParam(name = "itemId",
                                             value = "Id do pedido a ser alterado o status",
                                             example = "OPEN|WAITING_PAYMENT|IN_TRANSIT|CANCELLED",
                                             required = true)
                                             String orderId, @RequestBody @Valid PatchOrderRequest patchOrderRequest) {

        this.orderService.updateOrderStatus(bearerToken.substring(6), orderId, patchOrderRequest);
        return ResponseEntity.noContent().build();
    }
}
