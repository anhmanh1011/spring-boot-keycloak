package com.example.springbootkeycloak.ex;

import com.example.springbootkeycloak.model.response.ErrorsDto;
import com.example.springbootkeycloak.model.response.RestResponseDto;

public class RestBadRequestException extends RuntimeException {
    private final ErrorsDto errors;

    public RestBadRequestException(ErrorsDto errors) {
        this.errors = errors;
    }

    public RestResponseDto<ErrorsDto> getResponse() {
        return new RestResponseDto<ErrorsDto>().badRequest(errors);
    }
}
