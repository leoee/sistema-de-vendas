package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    ResponseEntity<User> createUser(@RequestBody @Valid User requestUserPayload) {
        return ResponseEntity.ok(this.userService.createUser(requestUserPayload));
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.userService.getUsers(bearerToken.substring(6)));
    }
}
