package com.puc.sistemasdevendas.model.repositories;

import com.puc.sistemasdevendas.model.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
}
