package com.example.springbootkeycloak.model.request;

import lombok.Data;

@Data
public class LoginRequest {
    String userName;
    String password;
}
