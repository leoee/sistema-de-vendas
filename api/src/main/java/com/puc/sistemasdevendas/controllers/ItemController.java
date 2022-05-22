package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.Item;
import com.puc.sistemasdevendas.model.services.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Tag;
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
import java.util.List;
import java.util.Optional;

@Controller
@CrossOrigin
@Api(tags = "Operações em itens")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "Criar item")
    @RequestMapping(value = "/items", method = RequestMethod.POST)
    ResponseEntity<Item> createItem(@RequestHeader("Authorization") final String bearerToken,
                                    @RequestBody @Valid Item requestItemPayload) {
        return new ResponseEntity<>(this.itemService
                .createItem(bearerToken.substring(6), requestItemPayload), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Buscar items cadastrados")
    @RequestMapping(value = "/items", method = RequestMethod.GET)
    ResponseEntity<List<Item>> getAllitems(@RequestParam("stockQuantity")
                                           @ApiParam(name = "stockQuantity",
                                                   value = "Parâmetro para retornar itens somente itens com estoque",
                                                   example = "true",
                                                   required = false)
                                                   Optional<Boolean> withStockQuantity,
                                           @RequestParam("minPrice")
                                           @ApiParam(name = "minPrice",
                                                   value = "Parâmetro para filtrar itens por preço mínimo",
                                                   example = "150",
                                                   required = false)
                                                   Optional<Integer> minPrice,
                                           @RequestParam("maxPrice")
                                           @ApiParam(name = "maxPrice",
                                                   value = "Parâmetro para filtrar itens por preço máximo",
                                                   example = "300",
                                                   required = false)
                                                   Optional<Integer> maxPrice,
                                           @RequestParam("name")
                                           @ApiParam(name = "maxPrice",
                                                   value = "Parâmetro para filtrar item por nome",
                                                   example = "Book X",
                                                   required = false)
                                                   Optional<String> name) {
        return ResponseEntity.ok(this.itemService.getAllItems(
                Boolean.TRUE.equals(withStockQuantity.orElse(null)),
                minPrice.orElse(null), maxPrice.orElse(null),
                name.orElse(null)));
    }

    @ApiOperation(value = "Apagar um item")
    @RequestMapping(value = "/items/{itemId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteItem(@RequestHeader("Authorization") final String bearerToken,
                                    @PathVariable
                                    @ApiParam(name = "itemId",
                                            value = "Valor do id do item a ser apagado",
                                            example = "112223331",
                                            required = true)
                                            String itemId) {
        try {
            this.itemService.deleteItem(bearerToken.substring(6), itemId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }
}
