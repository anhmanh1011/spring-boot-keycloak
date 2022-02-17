package com.example.springbootkeycloak.model.request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {

    String  currentPassword;
    String newPassword;
    String confirmPassword;

}
