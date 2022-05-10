package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.ShoppingCart;
import com.puc.sistemasdevendas.model.entities.ShoppingCartItemRequest;
import com.puc.sistemasdevendas.model.services.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@CrossOrigin
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

    @RequestMapping(value = "/shoppingCart/items/{itemId}", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteItem(@RequestHeader("Authorization") final String bearerToken,
                                 @PathVariable("itemId") String itemId) {
        this.shoppingCartService.deleteItemIntoSc(bearerToken.substring(6), itemId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/shoppingCart", method = RequestMethod.GET)
    ResponseEntity<ShoppingCart> getShoppingCart(@RequestHeader("Authorization") final String bearerToken,
                                                 @RequestParam("expand") Optional<Boolean> expand) {
        return ResponseEntity.ok(this.shoppingCartService.getShoppingCart(bearerToken.substring(6), expand.orElse(false)));
    }
}
