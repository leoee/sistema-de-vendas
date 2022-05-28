package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.PatchShoppingCartItem;
import com.puc.sistemasdevendas.model.entities.ShoppingCart;
import com.puc.sistemasdevendas.model.entities.ShoppingCartItemRequest;
import com.puc.sistemasdevendas.model.services.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
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
@Api(tags = "Operações em carrinho de compras")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @ApiOperation(value = "Adicionar item no carrinho de compras")
    @RequestMapping(value = "/shoppingCart", method = RequestMethod.POST)
    ResponseEntity<?> addItem(@RequestHeader("Authorization") final String bearerToken,
                              @RequestBody @Valid ShoppingCartItemRequest shoppingCartItem) {

        return new ResponseEntity<>(this.shoppingCartService.addItem(
                bearerToken.substring(6),
                shoppingCartItem.getItemId(),
                shoppingCartItem.getAmount()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Remover um item do carrinho de compras")
    @RequestMapping(value = "/shoppingCart/items/{itemId}", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteItem(@RequestHeader("Authorization") final String bearerToken,
                                 @PathVariable("itemId")
                                 @ApiParam(name = "itemId",
                                         value = "Id do item a ser removido no carrinho de compras",
                                         example = "111233",
                                         required = true)
                                         String itemId) {
        this.shoppingCartService.deleteItemIntoSc(bearerToken.substring(6), itemId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Editar a quantidade de um item do carrinho de compras")
    @RequestMapping(value = "/shoppingCart/items/{itemId}", method = RequestMethod.PATCH)
    ResponseEntity<?> updateItemQuantity(@RequestHeader("Authorization") final String bearerToken,
                                 @PathVariable("itemId")
                                 @ApiParam(name = "itemId",
                                         value = "Id do item a ser editado no carrinho de compras",
                                         example = "111233",
                                         required = true)
                                         String itemId,
                                         @RequestBody @Valid PatchShoppingCartItem shoppingCartItem) {
        return ResponseEntity.ok(this.shoppingCartService.updateItemAmount(bearerToken.substring(6), itemId, shoppingCartItem));
    }

    @ApiOperation(value = "Buscar um carrinho de compras")
    @RequestMapping(value = "/shoppingCart", method = RequestMethod.GET)
    ResponseEntity<ShoppingCart> getShoppingCart(@RequestHeader("Authorization") final String bearerToken,
                                                 @RequestParam("expand")
                                                 @ApiParam(name = "expand",
                                                         type = "String",
                                                         value = "Variável para controlar dados expandidos do carrinho de compras",
                                                         example = "true",
                                                         required = false)
                                                         Optional<Boolean> expand) {
        return ResponseEntity.ok(this.shoppingCartService.getShoppingCart(bearerToken.substring(6), expand.orElse(false)));
    }
}
