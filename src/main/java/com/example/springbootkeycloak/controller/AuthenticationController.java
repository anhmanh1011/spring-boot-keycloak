package com.example.springbootkeycloak.controller;

import com.example.springbootkeycloak.model.request.LoginRequest;
import com.example.springbootkeycloak.model.response.RestResponseDto;
import com.example.springbootkeycloak.service.KeyCloakService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Authentication")
@RestController
public class AuthenticationController {

    @Autowired
    KeyCloakService keyCloakService;

    @ApiOperation("Login")
    @PostMapping("/login")
    public RestResponseDto<AccessTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return keyCloakService.getUserJWT(loginRequest);
    }


}