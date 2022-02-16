package com.example.springbootkeycloak.controller;

import com.example.springbootkeycloak.model.request.LoginRequest;
import com.example.springbootkeycloak.model.response.RestResponseDto;
import com.example.springbootkeycloak.service.KeyCloakService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class AuthenticationController {

   @Autowired
    KeyCloakService keyCloakService;

    @PostMapping("/login")
    public RestResponseDto<AccessTokenResponse> login(LoginRequest loginRequest) {
        return keyCloakService.getUserJWT(loginRequest);
    }

    @GetMapping("/create/{userName}/{password}")
    public RestResponseDto<String> create(@PathVariable String userName, @PathVariable String password) {
        return keyCloakService.createUser(userName,password);
    }

}