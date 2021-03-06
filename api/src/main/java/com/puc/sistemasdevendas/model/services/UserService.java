package com.puc.sistemasdevendas.model.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.entities.UserPatch;
import com.puc.sistemasdevendas.model.exceptions.DuplicateEntity;
import com.puc.sistemasdevendas.model.exceptions.ForbidenException;
import com.puc.sistemasdevendas.model.helpers.DecodeToken;
import com.puc.sistemasdevendas.model.repositories.UserRepository;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.puc.sistemasdevendas.model.constants.ApiConstans.ADM_ROLE;
import static com.puc.sistemasdevendas.model.constants.ApiConstans.BASIC_ROLE;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private DecodeToken decodeToken;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Logger logger = Logger.getLogger(UserService.class);
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public User createUser(User user) {
        try {
            if (this.userRepository.findUserByEmail(user.getEmail()).isPresent()) {
                throw new DuplicateEntity("Email already in use");
            }

            this.logger.info("User created with email: " + user.getEmail());
            User createdUser = this.hideFields(this.userRepository.insert(this.setDefaultUserValues(user)));

            this.shoppingCartService.createShoppingCart(createdUser);
            return createdUser;
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

    public User updateUser(String jwtToken, String userId, UserPatch requestPayload) {
        String emailFromToken = this.decodeToken.getGetFromToken(jwtToken);
        Optional<User> fetchedUser = this.userRepository.findById(userId);

        if (fetchedUser.isEmpty() || !emailFromToken.equals(fetchedUser.get().getEmail())) {
            this.logger.warn("Not authorized to update user: " + userId);
            throw new ForbidenException("Not authorized to update user: " + userId);
        }

        if (requestPayload.getPassword() != null) {
            requestPayload.setPassword(bCryptPasswordEncoder.encode(requestPayload.getPassword()));
        }

        Update update = new Update();
        Query query = new Query().addCriteria(where("_id").is(userId));
        query.fields().exclude("password");
        Map<String, Object> dataMap = mapper.convertValue(requestPayload, Map.class);
        dataMap.values().removeIf(Objects::isNull);
        dataMap.forEach(update::set);

        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(true);

        return this.mongoTemplate.findAndModify(query, update, findAndModifyOptions, User.class);
    }

    public User getCurrentUser(String token) {
        String emailFromToken = this.decodeToken.getGetFromToken(token);
        User fetchedUser = this.userRepository.findUserByEmail(emailFromToken).orElse(null);

        return fetchedUser != null ? this.hideFields(fetchedUser) : null;
    }

    public boolean checkUserCredentials(final String email, final String password) {
        Optional<User> fetchedUser = this.userRepository.findUserByEmail(email);

        return fetchedUser.isPresent()
                && bCryptPasswordEncoder.matches(password, fetchedUser.get().getPassword());
    }

    public boolean isAdminRole(String email) {
        User fetchedUser = this.userRepository.findUserByEmail(email).orElse(null);

        return fetchedUser != null && ADM_ROLE.equals(fetchedUser.getRole());
    }

    private User setDefaultUserValues(User user) {
        user.setRole(BASIC_ROLE);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return user;
    }

    private User hideFields(User user) {
        user.setPassword(null);

        return user;
    }
}
