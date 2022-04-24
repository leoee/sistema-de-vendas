package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.exceptions.DuplicateEntity;
import com.puc.sistemasdevendas.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        try {
            // Validar se usuário tem permissão para criar outros
            if (this.userRepository.findUserByEmail(user.getEmail()).isPresent()) {
                throw new DuplicateEntity("Email already in use");
            }

            return this.userRepository.insert(user);
        } catch (Exception e) {
            throw e;
        }
    }
}
