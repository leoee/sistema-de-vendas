package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.ShoppingCart;
import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.repositories.ShoppingCartRepository;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    private final Logger logger = Logger.getLogger(ShoppingCartService.class);

    public void createShoppingCart(User owner) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOwner(owner.getId());
        shoppingCart.setTotal((double) 0);

        ShoppingCart createdSc = this.shoppingCartRepository.insert(shoppingCart);
        this.logger.info("Shopping cart created with id: " + createdSc.getId());
    }
}
