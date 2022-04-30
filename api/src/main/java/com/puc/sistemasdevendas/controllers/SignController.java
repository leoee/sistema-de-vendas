package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.UserLogin;
import com.puc.sistemasdevendas.model.services.SignInOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Controller
public class SignController {
    @Autowired
    private SignInOutService signService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    ResponseEntity<?> authorizeUser(@RequestBody @Valid UserLogin userPayload) throws UnsupportedEncodingException {
        return ResponseEntity.ok(this.signService.authorizeUser(userPayload));
    }
}
