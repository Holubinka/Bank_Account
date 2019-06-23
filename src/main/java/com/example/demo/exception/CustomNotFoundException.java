package com.example.demo.exception;

public class CustomNotFoundException extends RuntimeException {

    public CustomNotFoundException(String message) {
        super(message);
    }

}
