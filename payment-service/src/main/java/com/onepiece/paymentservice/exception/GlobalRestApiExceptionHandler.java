package com.onepiece.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalRestApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>>  handleMethodValidExceptions(MethodArgumentNotValidException ex) {
        Map<String,String> errorResponseMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach( error ->
                errorResponseMap.put(error.getField(), error.getDefaultMessage()));
        return  new ResponseEntity<>(errorResponseMap, HttpStatus.BAD_REQUEST);
    }
}
