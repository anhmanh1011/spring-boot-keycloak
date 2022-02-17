package com.example.springbootkeycloak.utils;

import com.example.springbootkeycloak.model.response.RestResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class FnCommon {

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Get message
     *
     * @param mess message
     * @return
     */
    public static String replaceMessWithValues(String mess, Map<String, String> parameter) {
        String result = mess;
        for (Map.Entry<String, String> entry : parameter.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Calendar today() {
        return Calendar.getInstance();
    }

    public static String todayStr() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return dateFormat.format(today().getTime());
    }

    public static String generatePinCode() {
        return String.format("%06d", secureRandom.nextInt(999999));
    }

    public static String hash256Hex(String data) {
        return DigestUtils.sha256Hex(data).toLowerCase();
    }

    public static String randomToken(final int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        String token = new String(Base64.encodeBase64(bytes));
        token = token.replaceAll("[^a-zA-Z0-9]", "");
        return token;
    }

    public static String toInternationalNumber(String phoneNumber, int countryCode) {
        if (phoneNumber.startsWith("0")) {
            return phoneNumber.replaceFirst("0", "+" + countryCode);
        }
        return phoneNumber;
    }

    public static String toNationalNumber(String phoneNumber, int countryCode) {
        if (phoneNumber.startsWith("+" + countryCode)) {
            return phoneNumber.replace("+" + countryCode, "0");
        }
        return phoneNumber;
    }

    public static String getRootURL(HttpServletRequest context) {
        return context.getRequestURL().toString().replace(context.getRequestURI(), "");
    }


    private static final List<CharacterRule> passwordCharacterRules = Arrays.asList(
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1)
    );

    private static final List<CharacterRule> passwordRules = Arrays.asList(
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1)
    );




}
