package com.example.springbootkeycloak.utils;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private  static JWTClaimsSet getClaimsFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        return claims;
    }

//    public static CustomsPrincipal getUserFromToken(String token) {
//
//        CustomsPrincipal user;
//        try {
//            JWTClaimsSet claims = getClaimsFromToken(token);
//            if (claims != null && isTokenExpired(claims)) {
//                user = new CustomsPrincipal(claims.getStringClaim("custodyCD"));
//                user.setEmail(claims.getStringClaim("email"));
//                user.setMobile(claims.getStringClaim("mobile"));
//                user.setFullName(claims.getStringClaim("fullName"));
//                user.setJti(claims.getStringClaim("jti"));
//                user.setBondCode(claims.getStringClaim("bondCode"));
//                user.setClient_id(claims.getStringClaim("client_id"));
//                user.setTokenId(claims.getJWTID());
//                return user;
//            }
//        } catch (Exception e) {
//            log.error( ErrorMessage.UNAUTHORIZED.getMessage());
//            throw new RestUnauthorizedException(RestError.newBuilder().addMessage(ErrorMessage.UNAUTHORIZED.getMessage()).build());
//        }
//        log.error( ErrorMessage.UNAUTHORIZED.getMessage());
//        throw new RestUnauthorizedException(RestError.newBuilder().addMessage(ErrorMessage.UNAUTHORIZED.getMessage()).build());
//
//    }

    private static Date getExpirationDateFromToken(JWTClaimsSet claims) {
        return claims != null ? claims.getExpirationTime() : new Date();
    }

    private static boolean isTokenExpired(JWTClaimsSet claims) {
        return getExpirationDateFromToken(claims).after(new Date());
    }
}
