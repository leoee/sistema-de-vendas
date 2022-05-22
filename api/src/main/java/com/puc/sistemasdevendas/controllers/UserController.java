package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.entities.UserPatch;
import com.puc.sistemasdevendas.model.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

@Controller
@CrossOrigin
@Api(tags = "Operações em usuários")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "Buscar usuário do token")
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.userService.getCurrentUser(bearerToken.substring(6)));
    }

    @ApiOperation(value = "Criar usuário")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    ResponseEntity<User> createUser(@RequestBody @Valid User requestUserPayload) {
        return new ResponseEntity<>(this.userService.createUser(requestUserPayload), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Buscar usuários")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.userService.getUsers(bearerToken.substring(6)));
    }

    @ApiOperation(value = "Atualizar dados de usuário")
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PATCH)
    ResponseEntity<User> updateUser(@PathVariable
                                    @ApiParam(name = "userId",
                                            value = "Valor do id do usuário a ser atualizado",
                                            example = "111233",
                                            required = true)
                                            String userId,
                                    @RequestBody @Valid UserPatch requestUserPayload,
                                    @RequestHeader("Authorization") final String bearerToken) {
        return ResponseEntity.ok(this.userService.updateUser(bearerToken.substring(6), userId, requestUserPayload));
    }
}
