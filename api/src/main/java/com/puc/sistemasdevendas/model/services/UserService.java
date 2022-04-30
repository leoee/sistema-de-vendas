package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.controllers.configurations.AuthorizeFilter;
import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.exceptions.DuplicateEntity;
import com.puc.sistemasdevendas.model.repositories.UserRepository;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.puc.sistemasdevendas.model.constants.ApiConstans.BASIC_ROLE;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = Logger.getLogger(AuthorizeFilter.class);

    public User createUser(User user) {
        try {
            if (this.userRepository.findUserByEmail(user.getEmail()).isPresent()) {
                throw new DuplicateEntity("Email already in use");
            }

            this.logger.info("User created with email: " + user.getEmail());
            return this.userRepository.insert(this.setDefaultUserValues(user));
        } catch (Exception e) {
            this.logger.error("Failed to create user: " + e.getMessage());
            throw e;
        }
    }

    public boolean checkUserCredentials(final String email, final String password) {
        Optional<User> fetchedUser = this.userRepository.findUserByEmail(email);

        if (fetchedUser.isPresent() && password.equals(fetchedUser.get().getPassword())) {
            return true;
        }

        return false;
    }

    private User setDefaultUserValues(User user) {
        user.setRole(BASIC_ROLE);

        return user;
    }
}
