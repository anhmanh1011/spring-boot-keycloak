package com.example.springbootkeycloak.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class PhoneNumberDto implements Serializable {

    @Schema(example = "84")
    private int countryCode;

    @Schema(example = "123456789")
    private Long nationalNumber;

    @JsonIgnore
    public String getInternationalNumber() {
        return "+" + countryCode + nationalNumber;
    }
}
