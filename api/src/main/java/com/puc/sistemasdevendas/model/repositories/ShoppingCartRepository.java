package com.puc.sistemasdevendas.model.repositories;

import com.puc.sistemasdevendas.model.entities.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, String> {
}
