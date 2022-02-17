package com.example.springbootkeycloak.controller;

import com.example.springbootkeycloak.model.request.CreateUserRequest;
import com.example.springbootkeycloak.model.request.UpdatePasswordRequest;
import com.example.springbootkeycloak.model.response.RestResponseDto;
import com.example.springbootkeycloak.service.KeyCloakService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Api( tags = "User")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    KeyCloakService keyCloakService;

    @ApiOperation("Create user")
    @PostMapping("")
    public RestResponseDto<String> create(@RequestBody CreateUserRequest createUserRequest) {
        return keyCloakService.createUser(createUserRequest);
    }

//    @ApiOperation("Get Role")
//    @GetMapping("/roles")
//    public RestResponseDto<List> getRoles() {
//        return keyCloakService.getAllRoles();
//    }

    @ApiOperation("Get User Info By userName")
    @GetMapping("/{userName}")
    public RestResponseDto<UserRepresentation> getUser(@PathVariable String userName) {
        return keyCloakService.getUserByUserName(userName);
    }

    @ApiOperation("Change pass")
    @PutMapping("/pass")
    public RestResponseDto<Object> changePassword(Principal principal, @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        return keyCloakService.changePass(principal,updatePasswordRequest);
    }

}
