package com.puc.sistemasdevendas.model.repositories;

import com.puc.sistemasdevendas.model.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query(value="{ 'email' : ?0 }")
    Optional<User> findUserByEmail(String email);
}
