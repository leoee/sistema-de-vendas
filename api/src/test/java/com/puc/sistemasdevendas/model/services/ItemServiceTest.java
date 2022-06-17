package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.Item;
import com.puc.sistemasdevendas.model.exceptions.ForbidenException;
import com.puc.sistemasdevendas.model.exceptions.InternalErrorException;
import com.puc.sistemasdevendas.model.exceptions.NotFoundException;
import com.puc.sistemasdevendas.model.helpers.DecodeToken;
import com.puc.sistemasdevendas.model.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@MockitoSettings
public class ItemServiceTest {
    @InjectMocks
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private DecodeToken decodeToken;

    @Test
    public void shouldCreateItem() {
        final String mockEmail = "mail@mail.com";
        Item expectedItem = new Item();

        when(this.decodeToken.getGetFromToken(any())).thenReturn(mockEmail);
        when(this.userService.isAdminRole(mockEmail)).thenReturn(true);
        when(this.itemRepository.insert((Item) any())).thenReturn(expectedItem);

        this.itemService.createItem("123", expectedItem);

        expectedItem.setActive(true);

        verify(this.itemRepository, times(1)).insert(eq(expectedItem));
    }

    @Test
    public void shouldThrowForbiddenExceptionBecauseIsNotAnAdm() {
        final String mockEmail = "mail@mail.com";
        Item expectedItem = new Item();

        when(this.decodeToken.getGetFromToken(any())).thenReturn(mockEmail);
        when(this.userService.isAdminRole(mockEmail)).thenReturn(false);

        assertThrows(ForbidenException.class,
                () ->  this.itemService.createItem("123", expectedItem));
    }

    @Test
    public void shouldThrowInternalErrorWhenInsertReturnedError() {
        final String mockEmail = "mail@mail.com";
        Item expectedItem = new Item();

        when(this.decodeToken.getGetFromToken(any())).thenReturn(mockEmail);
        when(this.userService.isAdminRole(mockEmail)).thenReturn(true);
        when(this.itemRepository.insert((Item) any())).thenThrow(InternalErrorException.class);

        assertThrows(InternalErrorException.class,
                () ->  this.itemService.createItem("123", expectedItem));
    }

    @Test
    public void shouldDeleteItem() {
        final String mockEmail = "mail@mail.com";
        Item expectedItem = new Item();

        when(this.decodeToken.getGetFromToken(any())).thenReturn(mockEmail);
        when(this.userService.isAdminRole(mockEmail)).thenReturn(true);
        when(this.itemRepository.findById(any())).thenReturn(Optional.of(expectedItem));

        this.itemService.deleteItem("123", "itemId");

        expectedItem.setActive(false);
        verify(this.itemRepository, times(1)).save(eq(expectedItem));
    }

    @Test
    public void shouldThrowNotFoundWhenInvalidItemId() {
        final String mockEmail = "mail@mail.com";

        when(this.decodeToken.getGetFromToken(any())).thenReturn(mockEmail);
        when(this.userService.isAdminRole(mockEmail)).thenReturn(true);
        when(this.itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () ->  this.itemService.deleteItem("123", "itemId"));
    }

    @Test
    public void shouldBuildQueryWithAllParameters() {
        final String expectedName = "name";
        final Integer minPrice = 100;
        final Integer maxPrice = 200;

        Query expectedQuery = new Query();
        expectedQuery.addCriteria(where("name").is(expectedName));
        expectedQuery.addCriteria(where("active").is(true));
        expectedQuery.addCriteria(where("stockQuantity").gte(1));
        expectedQuery.addCriteria(where("price").gte(minPrice).andOperator(where("price").lte(maxPrice)));

        this.itemService.getAllItems(true, minPrice, maxPrice, expectedName);

        verify(this.mongoTemplate, times(1)).find(eq(expectedQuery), any());

    }
}