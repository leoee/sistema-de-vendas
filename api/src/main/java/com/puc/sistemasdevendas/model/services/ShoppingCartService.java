package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.*;
import com.puc.sistemasdevendas.model.exceptions.BadRequestException;
import com.puc.sistemasdevendas.model.exceptions.ForbidenException;
import com.puc.sistemasdevendas.model.exceptions.NotFoundException;
import com.puc.sistemasdevendas.model.helpers.DecodeToken;
import com.puc.sistemasdevendas.model.repositories.ItemRepository;
import com.puc.sistemasdevendas.model.repositories.ShoppingCartRepository;
import com.puc.sistemasdevendas.model.repositories.UserRepository;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DecodeToken decodeToken;
    private final Logger logger = Logger.getLogger(ShoppingCartService.class);

    public void createShoppingCart(User owner) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(owner.getId());
        shoppingCart.setTotal((double) 0);

        ShoppingCart createdSc = this.shoppingCartRepository.insert(shoppingCart);
        this.logger.info("Shopping cart created with id: " + createdSc.getId());
    }

    public ShoppingCart addItem(String token, String itemId, Integer quantity) {
        try {
            // Needs refactor

            Item fetchedItem = this.itemRepository.findById(itemId).orElse(null);
            this.validateFetchedItem(fetchedItem, itemId);

            User fetchedUser = this.getUserFromToken(token);

            ShoppingCart fetchedSc = this.shoppingCartRepository.findCartByOwner(fetchedUser.getId()).get();

            if (fetchedSc.getShoppingCartItemList() == null) {
                fetchedSc.setShoppingCartItemList(new ArrayList<>());
            }

            fetchedSc.getShoppingCartItemList().forEach((shoppingCartItem -> {
                if (shoppingCartItem.getItemId().equals(itemId)) {
                    throw new BadRequestException("Shopping Cart already has this item " + itemId);
                }
            }));

            fetchedSc.setShoppingCartItemList(this.updateShoppingCartList(fetchedSc, fetchedItem, quantity));
            fetchedSc.setTotal(fetchedSc.getTotal() + fetchedItem.getPrice() * quantity);

            return this.shoppingCartRepository.save(fetchedSc);
        } catch (Exception e) {
            this.logger.error("Failed to add item into shoppingCart: " + e.getMessage());
            throw e;
        }

    }

    private void validateFetchedItem(Item fetchedItem, String itemId) {
        if (fetchedItem == null || fetchedItem.getStockQuantity() < 1) {
            this.logger.error("Could not add item " + itemId + " because it was not found or there is not stock");
            throw new NotFoundException("Not found or stock quantity for item " + itemId);
        }
    }

    private ShoppingCartItem buildNewShoppingCarItem(Item fetchedItem, Integer quantity) {

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setItemId(fetchedItem.getId());
        shoppingCartItem.setName(fetchedItem.getName());
        shoppingCartItem.setAmount(quantity);

        return shoppingCartItem;
    }

    private List<ShoppingCartItem> updateShoppingCartList(ShoppingCart fetchedSc, Item fetchedItem, Integer quantity) {
        List<ShoppingCartItem> currentScList = fetchedSc.getShoppingCartItemList() != null
                ? fetchedSc.getShoppingCartItemList()
                : new ArrayList<>();

        currentScList.add(this.buildNewShoppingCarItem(fetchedItem, quantity));

        return currentScList;
    }

    public void deleteItemIntoSc(String token, String itemId) {
        User fetchedUser = this.getUserFromToken(token);
        ShoppingCart fetchedSc = this.shoppingCartRepository.findCartByOwner(fetchedUser.getId()).orElse(null);
        Map<String, Integer> itemsInsideShoppingCart = new HashMap<>();

        if (fetchedSc == null) {
            throw new ForbidenException("Could not find shopping cart from user");
        }

        int sizeBeforeDelete = fetchedSc.getShoppingCartItemList().size();

        Item fetchedItem = this.itemRepository.findById(itemId).orElse(null);
        if (fetchedItem == null) {
            throw new NotFoundException("Not found item: " + itemId);
        }

        fetchedSc.setShoppingCartItemList(
                fetchedSc.getShoppingCartItemList()
                        .stream()
                        .filter((shoppingCartItem -> {
                            if (itemId.equals(shoppingCartItem.getItemId())) {
                                itemsInsideShoppingCart.put(itemId, shoppingCartItem.getAmount());
                                return false;
                            }
                            return true;
                        }))
                        .collect(Collectors.toList())
        );

        if (sizeBeforeDelete == fetchedSc.getShoppingCartItemList().size()) {
            throw new NotFoundException("Not found item inside shopping cart: " + itemId);
        }

        fetchedSc.setTotal(fetchedSc.getTotal() - (itemsInsideShoppingCart.get(itemId) * fetchedItem.getPrice()));

        this.shoppingCartRepository.save(fetchedSc);
    }

    public ShoppingCart getShoppingCart(String token, Boolean expandItems) {
        User fetchedUser = this.getUserFromToken(token);
        ShoppingCart fetchedSc = this.shoppingCartRepository.findCartByOwner(fetchedUser.getId()).orElse(null);
        if (fetchedSc == null) {
            this.logger.error("Failed to find shopping cart from user: " + fetchedUser.getId());
            throw new ForbidenException("Could not find shopping cart from user");
        }

        if (expandItems) {
            fetchedSc.getShoppingCartItemList().forEach(shoppingCartItem -> {
                Optional<Item> item = this.itemRepository.findById(shoppingCartItem.getItemId());
                shoppingCartItem.setItem(item.orElse(null));
            });
        }

        return fetchedSc;
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

    public ShoppingCart updateItemAmount(String token, String itemId, PatchShoppingCartItem shoppingCartItemPayload) {
        AtomicReference<Boolean> shoppingCartContainsItem = new AtomicReference<>(false);
        User fetchedUser = this.getUserFromToken(token);
        ShoppingCart fetchedSc = this.shoppingCartRepository.findCartByOwner(fetchedUser.getId()).orElse(null);

        if (fetchedSc != null && fetchedSc.getShoppingCartItemList() != null) {
            fetchedSc.getShoppingCartItemList().forEach(shoppingCartItem -> {
                if (shoppingCartItem.getItemId().equals(itemId)) {
                    shoppingCartContainsItem.set(true);
                    shoppingCartItem.setAmount(shoppingCartItemPayload.getAmount());
                }
            });
        }

        if (!shoppingCartContainsItem.get()) {
            this.logger.info("Could not found item to update quantity on shopping cart: itemId: "
                    + itemId + ", user: " + fetchedUser.getEmail());
            throw new NotFoundException("Could not find item on shopping cart to update");
        }

        return this.shoppingCartRepository.save(fetchedSc);
    }

}
