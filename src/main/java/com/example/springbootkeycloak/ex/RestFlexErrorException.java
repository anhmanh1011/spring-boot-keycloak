package com.example.springbootkeycloak.ex;

import com.example.springbootkeycloak.model.response.ErrorsDto;
import com.example.springbootkeycloak.model.response.RestResponseDto;

public class RestFlexErrorException extends RuntimeException {
    private final ErrorsDto errors;

    public RestFlexErrorException(ErrorsDto errors) {
        this.errors = errors;
    }

    public RestResponseDto<ErrorsDto> getResponse() {
        return new RestResponseDto<ErrorsDto>().flexError(errors);
    }
}
