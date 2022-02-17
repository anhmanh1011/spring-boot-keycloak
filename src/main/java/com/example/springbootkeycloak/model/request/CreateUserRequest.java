package com.example.springbootkeycloak.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserRequest  implements Serializable {

    String userName;
    String password;
    String email;
    String mobile;
    String firstName;
    String lastName;
}
