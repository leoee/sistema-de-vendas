package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.ShoppingCartItemRequest;
import com.puc.sistemasdevendas.model.services.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @RequestMapping(value = "/shoppingCart", method = RequestMethod.POST)
    ResponseEntity<?> addItem(@RequestHeader("Authorization") final String bearerToken,
                              @RequestBody @Valid ShoppingCartItemRequest shoppingCartItem) {

        return new ResponseEntity<>(this.shoppingCartService.addItem(
                bearerToken.substring(6),
                shoppingCartItem.getItemId(),
                shoppingCartItem.getQuantity()), HttpStatus.CREATED);
    }
}
