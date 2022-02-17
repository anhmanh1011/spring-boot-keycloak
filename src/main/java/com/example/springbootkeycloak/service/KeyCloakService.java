package com.example.springbootkeycloak.service;

import com.example.springbootkeycloak.ex.RestBadRequestException;
import com.example.springbootkeycloak.ex.RestServerErrorException;
import com.example.springbootkeycloak.ex.RestUnauthorizedException;
import com.example.springbootkeycloak.model.dto.PhoneNumberDto;
import com.example.springbootkeycloak.model.request.CreateUserRequest;
import com.example.springbootkeycloak.model.request.LoginRequest;
import com.example.springbootkeycloak.model.request.UpdatePasswordRequest;
import com.example.springbootkeycloak.model.response.ErrorsDto;
import com.example.springbootkeycloak.model.response.RestResponseDto;
import com.example.springbootkeycloak.utils.FnCommon;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KeyCloakService {
    @Value("${keycloak.credentials.secret}")
    private String secretKey;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.auth-server-url}")
    private String authUrl;
    @Value("${keycloak.realm}")
    private String realm;
//    @Value("${keycloak-admin.username}")
//    private String keycloakAdminUserName;
//
//    @Value("${keycloak-admin.password}")
//    private String keycloakAdminPassword;

    private Keycloak keycloak;

    @PostConstruct
    public Keycloak getInstance() {
        if (keycloak == null) {

            keycloak = KeycloakBuilder.builder()
                    .serverUrl(authUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                    .username(keycloakAdminUserName)
//                    .password(keycloakAdminPassword)
                    .clientId(clientId)
                    .clientSecret(secretKey)
                    .resteasyClient(new ResteasyClientBuilder()
                            .connectionPoolSize(10).build()
                    )
                    .build();
        }
        return keycloak;
    }

    public RestResponseDto<AccessTokenResponse> getUserJWT(LoginRequest loginRequest) {
        // log.trace(KssLogMarker.getLogstashLogMarker(), "start get jwt for user " + username);
        Keycloak keycloakUser = KeycloakBuilder.builder()
                .serverUrl(authUrl)
                .realm(realm)
                .username(loginRequest.getUserName())
                .password(loginRequest.getPassword())
                .grantType("password")
                .clientId(clientId)
                .clientSecret(secretKey)
                .build();

        try {
            AccessTokenResponse accessToken = keycloakUser.tokenManager().getAccessToken();
            log.info("login_success: " + loginRequest.getUserName());
            return new RestResponseDto<AccessTokenResponse>().success(accessToken);
        } catch (NotAuthorizedException e) {
            log.error(e.getMessage());
            throw new RestUnauthorizedException(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RestServerErrorException(ErrorsDto.newBuilder().addUsedField(e.getMessage()).build());
        }
    }

    public RestResponseDto<String> createUser(CreateUserRequest createUserRequest) {
        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(createUserRequest.getUserName());
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setEmail(createUserRequest.getEmail());
        user.setAttributes(Collections.singletonMap("mobile", Arrays.asList(createUserRequest.getMobile())));
        user.setEmailVerified(false);
        user.setEnabled(true);
        // set password
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(createUserRequest.getPassword());
        user.setCredentials(Collections.singletonList(passwordCred));


        // Get realm
        RealmResource realmResource = getInstance().realm(realm);
        UsersResource usersResource = realmResource.users();

        // Create user (requires manage-users role)
        Response response = usersResource.create(user);
        System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());

        if (response.getStatus() == 201) {
            System.out.println(response.getLocation());
            String userId = CreatedResponseUtil.getCreatedId(response);
            return new RestResponseDto<String>().success(userId);
        } else if (response.getStatus() == 409) {
            log.error("error_code_409");
            throw new RestUnauthorizedException(ErrorsDto.newBuilder().addMessage(response.getStatusInfo().getReasonPhrase()).build());
        } else {
            log.error("error_code_" + response.getStatus());
            throw new RestUnauthorizedException(ErrorsDto.newBuilder().addMessage("error_code_" + response.getStatus()).build());
        }
    }

    public RestResponseDto<List> getAllRoles() {
        ClientRepresentation clientRep = keycloak
                .realm(realm)
                .clients()
                .findByClientId(clientId)
                .get(0);
        List<String> availableRoles = keycloak
                .realm(realm)
                .clients()
                .get(clientRep.getId())
                .roles()
                .list()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
        return new RestResponseDto<List>().success(availableRoles);
    }

    public RestResponseDto<UserRepresentation> getUserByUserName(String userName) {
        List<UserRepresentation> search = keycloak
                .realm(realm)
                .users()
                .search(userName);
        UserRepresentation userRepresentation = search.get(0);
        return new RestResponseDto<UserRepresentation>().success(userRepresentation);
    }


    public RestResponseDto<Object> changePass(Principal principal, UpdatePasswordRequest updatePasswordRequest) {


        try {
            RestResponseDto<AccessTokenResponse> userJWT = this.getUserJWT(new LoginRequest(principal.getName(), updatePasswordRequest.getCurrentPassword()));
            UsersResource users = keycloak.realm(realm).users();
            UserRepresentation userRepresentation = users.search(principal.getName()).get(0);
            String id = userRepresentation.getId();
            UserResource userResource = keycloak.realm(realm).users().get(id);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(updatePasswordRequest.getConfirmPassword());
            userResource.resetPassword(passwordCred);
            return new RestResponseDto<>().success();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RestBadRequestException(ErrorsDto.newBuilder().addMessage(e.getMessage()).build());
        }


    }


    public String getUserIdByEmail(String email) {
        // log.trace(KssLogMarker.getLogstashLogMarker(), "start get userId by email " + email);
        UsersResource usersResource = keycloak.realm(realm).users();
        List<UserRepresentation> users = usersResource.search(null, null, null, email, null, null);
        UserRepresentation userId = users.stream().filter(user -> user.getEmail().equals(email)).findFirst().orElse(null);
        return userId != null ? userId.getId() : null;
    }

    public String getUserIdByPhoneNumber(PhoneNumberDto phoneNumberDto) {
        // log.trace(KssLogMarker.getLogstashLogMarker(), "start get userId by phone number " + phoneNumberDto.getInternationalNumber());
        UsersResource usersResource = keycloak.realm(realm).users();
        List<UserRepresentation> users = usersResource.search(phoneNumberDto.getInternationalNumber(), true);
        UserRepresentation userId = users.stream().filter(user -> user.getUsername().equals(phoneNumberDto.getInternationalNumber())).findFirst().orElse(null);

        return userId != null ? userId.getId() : null;
    }

    public String getUserIdByPhoneNumber(String phoneNumber) {
        // log.trace(KssLogMarker.getLogstashLogMarker(), "start get userId by phone number " + phoneNumber);
        UsersResource usersResource = keycloak.realm(realm).users();
        List<UserRepresentation> users = usersResource.search(phoneNumber, true);
        UserRepresentation userId = users.stream().filter(user -> user.getUsername().equals(phoneNumber)).findFirst().orElse(null);

        return userId != null ? userId.getId() : null;
    }
}
