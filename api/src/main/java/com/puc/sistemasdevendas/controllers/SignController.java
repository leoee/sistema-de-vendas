package com.puc.sistemasdevendas.controllers;

import com.puc.sistemasdevendas.model.entities.AuthenticationResponse;
import com.puc.sistemasdevendas.model.entities.UserLogin;
import com.puc.sistemasdevendas.model.services.SignInOutService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Controller
@CrossOrigin
@Api(tags = "Operação de login")
public class SignController {
    @Autowired
    private SignInOutService signService;

    @ApiOperation(value = "Gerar token de usuário")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    ResponseEntity<AuthenticationResponse> authorizeUser(@RequestBody @Valid UserLogin userPayload) throws UnsupportedEncodingException {
        return ResponseEntity.ok(new AuthenticationResponse(this.signService.authorizeUser(userPayload)));
    }
}
