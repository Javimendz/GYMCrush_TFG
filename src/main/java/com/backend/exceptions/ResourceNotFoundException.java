package com.backend.exceptions;
//Excepcion personalizada
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}