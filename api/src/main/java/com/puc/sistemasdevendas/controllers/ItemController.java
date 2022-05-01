package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.Item;
import com.puc.sistemasdevendas.model.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/items", method = RequestMethod.POST)
    ResponseEntity<Item> createItem(@RequestHeader("Authorization") final String bearerToken,
                                    @RequestBody @Valid Item requestItemPayload) {
        return ResponseEntity.ok(this.itemService.createItem(bearerToken.substring(6), requestItemPayload));
    }

    @RequestMapping(value = "/items", method = RequestMethod.GET)
    ResponseEntity<List<Item>> getAllitems(@RequestParam("stockQuantity") Optional<Boolean> withStockQuantity,
                                           @RequestParam("minPrice") Optional<Integer> minPrice,
                                           @RequestParam("maxPrice") Optional<Integer> maxPrice,
                                           @RequestParam("name") Optional<String> name) {
        return ResponseEntity.ok(this.itemService.getAllItems(
                Boolean.TRUE.equals(withStockQuantity.orElse(null)),
                minPrice.orElse(null), maxPrice.orElse(null),
                name.orElse(null)));
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
}
