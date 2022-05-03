package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.Item;
import com.puc.sistemasdevendas.model.entities.ShoppingCart;
import com.puc.sistemasdevendas.model.entities.ShoppingCartItem;
import com.puc.sistemasdevendas.model.entities.User;
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

import java.util.ArrayList;
import java.util.List;
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
            String emailFromToken = this.decodeToken.getGetFromToken(token);

            Item fetchedItem = this.itemRepository.findById(itemId).orElse(null);
            this.validateFetchedItem(fetchedItem, itemId);

            User shoppingCartOwner = this.userRepository.findUserByEmail(emailFromToken).orElse(null);
            this.validateUser(shoppingCartOwner, emailFromToken);

            ShoppingCart fetchedSc = this.shoppingCartRepository.findCartByOwner(shoppingCartOwner.getId()).get();

            fetchedSc.getShoppingCartItemList().forEach((shoppingCartItem -> {
                if (shoppingCartItem.getItemId().equals(itemId)) {
                    throw new BadRequestException("Shopping Cart already has this item " + itemId);
                }
            }));

            fetchedSc.setShoppingCartItemList(this.updateShoppingCarList(fetchedSc, fetchedItem, quantity));
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

    private void validateUser(User shoppingCartOwner, String emailFromToken) {
        if (shoppingCartOwner == null) {
            this.logger.error("Could not find user with email: " + emailFromToken);
            throw new ForbidenException("Invalid token");
        }
    }

    private ShoppingCartItem buildNewShoppingCarItem(Item fetchedItem, Integer quantity) {

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setItemId(fetchedItem.getId());
        shoppingCartItem.setName(fetchedItem.getName());
        shoppingCartItem.setAmount(quantity);

        return shoppingCartItem;
    }

    private List<ShoppingCartItem> updateShoppingCarList(ShoppingCart fetchedSc, Item fetchedItem, Integer quantity) {
        List<ShoppingCartItem> currentScList = fetchedSc.getShoppingCartItemList() != null
                ? fetchedSc.getShoppingCartItemList()
                : new ArrayList<>();

        currentScList.add(this.buildNewShoppingCarItem(fetchedItem, quantity));

        return currentScList;
    }

    public void deleteItemIntoSc(String token, String itemId) {
        String emailFromToken = this.decodeToken.getGetFromToken(token);

        User fetchedUser = this.userRepository.findUserByEmail(emailFromToken).orElse(null);
        if (fetchedUser == null) {
            this.logger.error("Could not get user by email from token: " + emailFromToken);
            throw new ForbidenException("Could not get user from token");
        }
        ShoppingCart fetchedSc = this.shoppingCartRepository.findCartByOwner(fetchedUser.getId()).orElse(null);
        if (fetchedSc == null) {
            throw new ForbidenException("Could not find shopping cart from user");
        }

        int sizeBeforeDelete = fetchedSc.getShoppingCartItemList().size();

        fetchedSc.setShoppingCartItemList(
                fetchedSc.getShoppingCartItemList()
                        .stream()
                        .filter((shoppingCartItem -> !itemId.equals(shoppingCartItem.getItemId())))
                        .collect(Collectors.toList())
        );

        if (sizeBeforeDelete == fetchedSc.getShoppingCartItemList().size()) {
            throw new NotFoundException("Not found item inside shopping cart: " + itemId);
        }

        this.shoppingCartRepository.save(fetchedSc);
    }

}
