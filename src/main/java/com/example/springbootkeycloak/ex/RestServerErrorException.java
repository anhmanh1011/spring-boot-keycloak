package com.example.springbootkeycloak.ex;

import com.example.springbootkeycloak.model.response.ErrorsDto;
import com.example.springbootkeycloak.model.response.RestResponseDto;

public class RestServerErrorException extends RuntimeException {
    private final ErrorsDto errors;

    public RestServerErrorException(ErrorsDto errors) {
        this.errors = errors;
    }

    public RestResponseDto<ErrorsDto> getResponse() {
        return new RestResponseDto<ErrorsDto>().serverError(errors);
    }
}
