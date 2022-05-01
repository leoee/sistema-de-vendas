package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.Item;
import com.puc.sistemasdevendas.model.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/items", method = RequestMethod.POST)
    ResponseEntity<Item> createItem(@RequestHeader("Authorization") final String bearerToken,
                                    @RequestBody @Valid Item requestItemPayload) {
        return ResponseEntity.ok(this.itemService.createItem(bearerToken.substring(6), requestItemPayload));
    }

    @RequestMapping(value = "/items/{itemId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteItem(@RequestHeader("Authorization") final String bearerToken,
                                    @PathVariable String itemId) {
        try {
            this.itemService.deleteItem(bearerToken.substring(6), itemId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }

    /*@RequestMapping(value = "/users", method = RequestMethod.GET)
    ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.userService.getUsers(bearerToken.substring(6)));
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PATCH)
    ResponseEntity<User> updateUser(@PathVariable String userId,
                                    @RequestBody @Valid UserPatch requestUserPayload,
                                    @RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.userService.updateUser(bearerToken.substring(6), userId, requestUserPayload));
    }*/
}
