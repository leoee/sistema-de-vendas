package com.puc.sistemasdevendas.model.repositories;

import com.puc.sistemasdevendas.model.entities.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    @Query(value="{ 'stockQuantity' : {$gt: 0} }")
    Optional<List<Item>> findAllWithStockQuantity();
}
