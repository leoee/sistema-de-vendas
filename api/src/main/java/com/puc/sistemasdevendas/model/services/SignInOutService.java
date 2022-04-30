package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.UserLogin;
import com.puc.sistemasdevendas.model.exceptions.UserNotAuthorizedException;
import com.puc.sistemasdevendas.model.helpers.ApplicationProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class SignInOutService {
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private UserService userService;
    private final Logger logger = Logger.getLogger(SignInOutService.class);

    public String authorizeUser(UserLogin userPayload) throws UnsupportedEncodingException {
        try {
            if (this.userService.checkUserCredentials(userPayload.getEmail(), userPayload.getPassword())) {
                String jwtToken = Jwts.builder()
                        .setSubject(userPayload.getEmail())
                        .setIssuer("localhost:8080")
                        .setIssuedAt(new Date())
                        .setExpiration(
                                Date.from(
                                        LocalDateTime.now().plusHours(8L)
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()))
                        .signWith(
                                SignatureAlgorithm.HS256,
                                this.applicationProperties.getJwtKey().getBytes("UTF-8")
                        )
                        .compact();

                return jwtToken;
            } else {
                this.logger.warn("An attempt to authorize was made | " + userPayload.getEmail());
                throw new UserNotAuthorizedException("Username and/or password invalid");
            }
        } catch (Exception ex) {
            this.logger.error("Failed to authorize user: " + ex.getMessage());
            throw ex;
        }
    }
}
