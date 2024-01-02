package com.example.ecommerce.exception;

public class UsernameOrEmailAlreadyTakenException extends RuntimeException{
    public UsernameOrEmailAlreadyTakenException(String message) {
        super(message);
    }
}
