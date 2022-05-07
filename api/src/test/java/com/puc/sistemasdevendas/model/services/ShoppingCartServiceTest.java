package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.Item;
import com.puc.sistemasdevendas.model.entities.ShoppingCart;
import com.puc.sistemasdevendas.model.entities.ShoppingCartItem;
import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.exceptions.ForbidenException;
import com.puc.sistemasdevendas.model.exceptions.NotFoundException;
import com.puc.sistemasdevendas.model.helpers.DecodeToken;
import com.puc.sistemasdevendas.model.repositories.ItemRepository;
import com.puc.sistemasdevendas.model.repositories.ShoppingCartRepository;
import com.puc.sistemasdevendas.model.repositories.UserRepository;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@MockitoSettings
public class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartService shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private DecodeToken decodeToken;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Logger logger;

    @Test
    public void shouldCreateShoppingCart() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setId("123");

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(user.getId());
        shoppingCart.setTotal((double) 0);

        when(this.shoppingCartRepository.insert((ShoppingCart) any())).thenReturn(shoppingCart);

        this.shoppingCartService.createShoppingCart(user);

        verify(this.shoppingCartRepository, times(1)).insert(shoppingCart);
    }

    @Test
    public void shouldAddOneItemOnShoppingCart() {
        final String email = "test@mail.com";
        final String userId = "12331231231";
        final String itemId = "123-3322-2";
        final String itemName = "itemName";
        final Integer quantity = 1;
        final Double price = 33.0;

        Item mockedItem = this.mockItem(itemId, itemName, price);

        User user = new User();
        user.setId(userId);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(userId);
        shoppingCart.setTotal((double) 0);

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.itemRepository.findById(itemId)).thenReturn(Optional.of(mockedItem));
        when(this.shoppingCartRepository.findCartByOwner(userId)).thenReturn(Optional.of(shoppingCart));
        when(this.userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        this.shoppingCartService.addItem("123", itemId, quantity);

        ShoppingCart expectedShoppingCart = new ShoppingCart();
        expectedShoppingCart.setOwner(userId);
        expectedShoppingCart.setTotal(quantity * mockedItem.getPrice());

        expectedShoppingCart.setShoppingCartItemList(List.of(this.mockShoppingCarItem(itemName, itemId, quantity)));

        verify(this.shoppingCartRepository, times(1)).save(expectedShoppingCart);
    }

    @Test
    public void shouldAddItemWithQuantity2OnShoppingCart() {
        final String email = "test@mail.com";
        final String userId = "12331231231";
        final String itemId = "123-3322-2";
        final String itemName = "itemName";
        final Integer quantity = 2;
        final Double price = 33.0;

        Item mockedItem = this.mockItem(itemId, itemName, price);

        User user = new User();
        user.setId(userId);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(userId);
        shoppingCart.setTotal((double) 0);

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.itemRepository.findById(itemId)).thenReturn(Optional.of(mockedItem));
        when(this.shoppingCartRepository.findCartByOwner(userId)).thenReturn(Optional.of(shoppingCart));
        when(this.userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        this.shoppingCartService.addItem("123", itemId, quantity);

        ShoppingCart expectedShoppingCart = new ShoppingCart();
        expectedShoppingCart.setOwner(userId);
        expectedShoppingCart.setTotal(quantity * mockedItem.getPrice());

        expectedShoppingCart.setShoppingCartItemList(List.of(this.mockShoppingCarItem(itemName, itemId, quantity)));

        verify(this.shoppingCartRepository, times(1)).save(expectedShoppingCart);
    }

    @Test
    public void shouldAddOneItemOnExistingShoppingCart() {
        final String email = "test@mail.com";
        final String userId = "12331231231";
        final String itemId = "123-3322-1";
        final String itemId2 = "123-3322-2";
        final String itemName = "itemName";
        final Integer quantity = 1;
        final Double price = 33.0;

        Item mockedItem = this.mockItem(itemId2, itemName, price);

        User user = new User();
        user.setId(userId);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(userId);
        shoppingCart.setTotal(mockedItem.getPrice());
        List<ShoppingCartItem> shoppingCartItemList = new ArrayList<>();
        shoppingCartItemList.add(this.mockShoppingCarItem(itemName, itemId, quantity));

        shoppingCart.setShoppingCartItemList(shoppingCartItemList);

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.itemRepository.findById(any())).thenReturn(Optional.of(mockedItem));
        when(this.shoppingCartRepository.findCartByOwner(userId)).thenReturn(Optional.of(shoppingCart));
        when(this.userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        this.shoppingCartService.addItem("123", itemId2, quantity);

        ShoppingCart expectedShoppingCart = new ShoppingCart();
        expectedShoppingCart.setOwner(userId);
        expectedShoppingCart.setTotal((quantity * mockedItem.getPrice()) + mockedItem.getPrice());

        expectedShoppingCart.setShoppingCartItemList(
                Arrays.asList(
                        this.mockShoppingCarItem(itemName, itemId, quantity),
                        this.mockShoppingCarItem(itemName, itemId2, quantity))
        );


        verify(this.shoppingCartRepository, times(1)).save(expectedShoppingCart);
    }

    @Test
    public void shouldThrowNotFoundItem() {
        when(this.itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () ->  this.shoppingCartService.addItem("123", "3333", 1));
    }

    @Test
    public void shouldThrowForbiddenWhenNotFoundUser() {
        final String itemId = "itemId";
        final String itemName = "itemName";
        final Double price = 11.0;
        final String email = "test@mail.com";

        Item mockedItem = this.mockItem(itemId, itemName, price);

        when(this.itemRepository.findById(any())).thenReturn(Optional.of(mockedItem));
        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.empty());

        assertThrows(ForbidenException.class,
                () ->  this.shoppingCartService.addItem("123", "3333", 1));
    }

    @Test
    public void shouldDeleteItemOnSc() {
        final String email = "test@mail.com";
        final String userId = "userId";
        final String itemId = "itemId";
        final String itemName = "itemName";
        final Double price = 11.0;

        Item mockedItem = this.mockItem(itemId, itemName, price);

        User user = new User();
        user.setId(userId);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(userId);
        shoppingCart.setTotal(mockedItem.getPrice());

        shoppingCart.setShoppingCartItemList(List.of(this.mockShoppingCarItem(itemName, itemId, 1)));

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        when(this.itemRepository.findById(any())).thenReturn(Optional.of(mockedItem));
        when(this.shoppingCartRepository.findCartByOwner(userId)).thenReturn(Optional.of(shoppingCart));

        this.shoppingCartService.deleteItemIntoSc("123", itemId);

        ShoppingCart expectedShoppingCart = new ShoppingCart();
        expectedShoppingCart.setOwner(userId);
        expectedShoppingCart.setTotal((double) 0);
        expectedShoppingCart.setShoppingCartItemList(new ArrayList<>());

        verify(this.shoppingCartRepository, times(1)).save(expectedShoppingCart);
    }

    @Test
    public void shouldDeleteItemOnScWithMultipleItems() {
        final String email = "test@mail.com";
        final String userId = "userId";
        final String itemId = "itemId";
        final String itemId2 = "itemId2";
        final String itemName = "itemName";
        final Double price = 11.0;
        final Double price2 = 99.0;
        final Integer quantityItem2 = 2;

        Item mockedItem = this.mockItem(itemId, itemName, price);
        Item mockedItem2 = this.mockItem(itemId2, itemName, price2);

        User user = new User();
        user.setId(userId);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(userId);
        shoppingCart.setTotal(mockedItem.getPrice() + mockedItem2.getPrice() * quantityItem2);

        shoppingCart.setShoppingCartItemList(
                Arrays.asList(
                        this.mockShoppingCarItem(itemName, itemId, 1),
                        this.mockShoppingCarItem(itemName, itemId2, quantityItem2)
                )
        );

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        when(this.itemRepository.findById(any())).thenReturn(Optional.of(mockedItem2));
        when(this.shoppingCartRepository.findCartByOwner(userId)).thenReturn(Optional.of(shoppingCart));

        this.shoppingCartService.deleteItemIntoSc("123", itemId2);

        ShoppingCart expectedShoppingCart = new ShoppingCart();
        expectedShoppingCart.setOwner(userId);
        expectedShoppingCart.setTotal(mockedItem.getPrice());
        expectedShoppingCart
                .setShoppingCartItemList(List.of(this.mockShoppingCarItem(itemName, itemId, 1)));

        verify(this.shoppingCartRepository, times(1)).save(expectedShoppingCart);
    }


    @Test
    public void shouldThrowNotFoundWhenDeletingInvalidItem() {
        final String email = "test@mail.com";
        final String userId = "userId";
        final String itemId = "itemId";
        final String itemName = "itemName";
        final Double price = 11.0;

        Item mockedItem = this.mockItem(itemId, itemName, price);

        User user = new User();
        user.setId(userId);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(userId);
        shoppingCart.setTotal(mockedItem.getPrice());

        shoppingCart.setShoppingCartItemList(List.of(this.mockShoppingCarItem(itemName, itemId, 1)));

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        when(this.itemRepository.findById(any())).thenReturn(Optional.of(mockedItem));
        when(this.shoppingCartRepository.findCartByOwner(userId)).thenReturn(Optional.of(shoppingCart));

        assertThrows(NotFoundException.class,
                () ->  this.shoppingCartService.deleteItemIntoSc("123", "invalidId"));
    }

    @Test
    public void shouldGetShoppingCart() {
        final String email = "test@mai.com";
        final String userId = "userId";
        final Double price = 11.0;
        final String itemName = "itemName";
        final String itemId = "itemId";

        User mockedUser = new User();
        mockedUser.setId(userId);

        ShoppingCart mockedSc = new ShoppingCart();
        mockedSc.setOwner(userId);
        mockedSc.setTotal(price);
        mockedSc.setShoppingCartItemList(List.of(this.mockShoppingCarItem(itemName, itemId, 1)));

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.shoppingCartRepository.findCartByOwner(any())).thenReturn(Optional.of(mockedSc));
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));

        ShoppingCart fetchedSc = this.shoppingCartService.getShoppingCart("123", false);

        assertEquals(itemName, fetchedSc.getShoppingCartItemList().get(0).getName());
        assertEquals(itemId, fetchedSc.getShoppingCartItemList().get(0).getItemId());
        assertNull(fetchedSc.getShoppingCartItemList().get(0).getItem());
    }

    @Test
    public void shouldGetShoppingCartWithExpandedItem() {
        final String email = "test@mai.com";
        final String userId = "userId";
        final Double price = 11.0;
        final String itemName = "itemName";
        final String itemId = "itemId";

        User mockedUser = new User();
        mockedUser.setId(userId);

        Item mockedItem = new Item();
        mockedItem.setId(itemId);
        mockedItem.setName(itemName);
        mockedItem.setPrice(price);

        ShoppingCart mockedSc = new ShoppingCart();
        mockedSc.setOwner(userId);
        mockedSc.setTotal(price);
        mockedSc.setShoppingCartItemList(List.of(this.mockShoppingCarItem(itemName, itemId, 1)));

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.shoppingCartRepository.findCartByOwner(any())).thenReturn(Optional.of(mockedSc));
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));
        when(this.itemRepository.findById(itemId)).thenReturn(Optional.of(mockedItem));

        ShoppingCart fetchedSc = this.shoppingCartService.getShoppingCart("123", true);

        assertEquals(itemName, fetchedSc.getShoppingCartItemList().get(0).getName());
        assertEquals(itemId, fetchedSc.getShoppingCartItemList().get(0).getItemId());
        assertNotNull(fetchedSc.getShoppingCartItemList().get(0).getItem());
    }

    private Item mockItem(String itemId, String itemName, Double price) {
        Item mockedItem = new Item();
        mockedItem.setId(itemId);
        mockedItem.setStockQuantity(1);
        mockedItem.setName(itemName);
        mockedItem.setPrice(price);

        return mockedItem;
    }

    private ShoppingCartItem mockShoppingCarItem(String itemName, String itemId, Integer quantity) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setName(itemName);
        shoppingCartItem.setItemId(itemId);
        shoppingCartItem.setAmount(quantity);

        return shoppingCartItem;
    }


}