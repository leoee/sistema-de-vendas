package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.entities.UserPatch;
import com.puc.sistemasdevendas.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    ResponseEntity<User> createUser(@RequestBody @Valid User requestUserPayload) {
        return new ResponseEntity<>(this.userService.createUser(requestUserPayload), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.userService.getUsers(bearerToken.substring(6)));
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PATCH)
    ResponseEntity<User> updateUser(@PathVariable String userId,
                                    @RequestBody @Valid UserPatch requestUserPayload,
                                    @RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.userService.updateUser(bearerToken.substring(6), userId, requestUserPayload));
    }
}
