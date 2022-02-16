package com.example.springbootkeycloak.service;

import com.example.springbootkeycloak.ex.RestBadRequestException;
import com.example.springbootkeycloak.ex.RestServerErrorException;
import com.example.springbootkeycloak.ex.RestUnauthorizedException;
import com.example.springbootkeycloak.model.request.LoginRequest;
import com.example.springbootkeycloak.model.response.ErrorsDto;
import com.example.springbootkeycloak.model.response.RestResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

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
    @Value("${keycloak-admin.username}")
    private String keycloakAdminUserName;

    @Value("${keycloak-admin.password}")
    private String keycloakAdminPassword;

    private Keycloak keycloak;

    @PostConstruct
    public Keycloak getInstance() {
        if (keycloak == null) {

            keycloak = KeycloakBuilder.builder()
                    .serverUrl(authUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(keycloakAdminUserName)
                    .password(keycloakAdminPassword)
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

    public RestResponseDto<String> createUser(String userName, String password) {
        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userName);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail(userName + "tester1@tdlabs.local");
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));
        user.setEmailVerified(false);
        user.setEnabled(true);
        // set password
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);

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
            throw new RestBadRequestException(ErrorsDto.newBuilder().addMessage(response.getStatusInfo().getReasonPhrase()).build());
        } else {
            log.error("error_code_" + response.getStatus());
            throw new RestBadRequestException(ErrorsDto.newBuilder().addMessage("error_code_" + response.getStatus()).build());
        }
    }
}
