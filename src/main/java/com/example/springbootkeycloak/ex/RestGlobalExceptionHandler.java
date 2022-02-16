package com.example.springbootkeycloak.ex;

import com.example.springbootkeycloak.model.response.ErrorsDto;
import com.example.springbootkeycloak.model.response.RestResponseDto;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@EnableWebMvc
@ControllerAdvice
public class RestGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private RestResponseDto<ErrorsDto> getResponseBody(HttpStatus status, Map<String, List<String>> errors) {
        return new RestResponseDto<ErrorsDto>()
            .setStatus(status.value())
            .setMessage(status.getReasonPhrase())
            .setData(errors != null ? ErrorsDto.newBuilder(errors).build() : null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
        HttpMediaTypeNotAcceptableException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
        MissingPathVariableException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
        ServletRequestBindingException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(
        ConversionNotSupportedException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        TypeMismatchException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
        HttpMessageNotWritableException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), Arrays.asList(error.getDefaultMessage().split("@")));
        });
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, errors));
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
        MissingServletRequestPartException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleBindException(
        BindException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), Arrays.asList(error.getDefaultMessage().split("@")));
        });
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, errors));
    }


    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
        NoHandlerFoundException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }


    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
        AsyncRequestTimeoutException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(getResponseBody(status, null));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public RestResponseDto<Object> handle(ConstraintViolationException ex) {
        Optional<ConstraintViolation<?>> constraintViolation = ex.getConstraintViolations().stream().findFirst();
        if (constraintViolation.isPresent()) {
            String message = constraintViolation.get().getMessage();
            return new RestResponseDto<>().badRequest(message);
        }
        return new RestResponseDto<>().badRequest();
    }

    @ExceptionHandler({RestBadRequestException.class})
    public ResponseEntity<Object> handleRestAPIException(RestBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(ex.getResponse());
    }

    @ExceptionHandler({RestFlexErrorException.class})
    public ResponseEntity<Object> handleRestAPIException(RestFlexErrorException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(ex.getResponse());
    }

    @ExceptionHandler({RestNotFoundException.class})
    public ResponseEntity<Object> handleRestAPIException(RestNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(ex.getResponse());
    }

    @ExceptionHandler({RestNotAllowedException.class})
    public ResponseEntity<Object> handleRestAPIException(RestNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(ex.getResponse());
    }

    @ExceptionHandler({RestServerErrorException.class})
    public ResponseEntity<Object> handleRestAPIException(RestServerErrorException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(ex.getResponse());
    }

    @ExceptionHandler({RestUnauthorizedException.class})
    public ResponseEntity<Object> handleRestAPIException(RestUnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(ex.getResponse());
    }
}
