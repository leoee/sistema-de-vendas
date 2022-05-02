package com.puc.sistemasdevendas.model.repositories;

import com.puc.sistemasdevendas.model.entities.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, String> {
    @Query(value="{ 'owner' : ?0 }")
    Optional<ShoppingCart> findCartByOwner(String owner);
}
