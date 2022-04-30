package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.controllers.configurations.AuthorizeFilter;
import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.exceptions.DuplicateEntity;
import com.puc.sistemasdevendas.model.exceptions.ForbidenException;
import com.puc.sistemasdevendas.model.helpers.DecodeToken;
import com.puc.sistemasdevendas.model.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static com.puc.sistemasdevendas.model.constants.ApiConstans.ADM_ROLE;
import static com.puc.sistemasdevendas.model.constants.ApiConstans.BASIC_ROLE;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DecodeToken decodeToken;
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

    public List<User> getUsers(String token) {
        String emailFromToken = this.decodeToken.getGetFromToken(token);

        if (!this.isAdminRole(emailFromToken)) {
            this.logger.warn("User not authorized to get all users: " + emailFromToken);
            throw new ForbidenException("User not authorized to perform this operation");
        }
        return this.userRepository.findAll();
    }

    public boolean checkUserCredentials(final String email, final String password) {
        Optional<User> fetchedUser = this.userRepository.findUserByEmail(email);

        return fetchedUser.isPresent() && password.equals(fetchedUser.get().getPassword());
    }

    public boolean isAdminRole(String email) {
        Optional<User> fetchedUser = this.userRepository.findUserByEmail(email);

        return fetchedUser.isPresent() && ADM_ROLE.equals(fetchedUser.get().getRole());
    }

    private User setDefaultUserValues(User user) {
        user.setRole(BASIC_ROLE);

        return user;
    }
}
