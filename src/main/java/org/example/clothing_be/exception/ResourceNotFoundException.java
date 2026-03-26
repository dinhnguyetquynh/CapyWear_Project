package org.example.clothing_be.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends NotFoundException{
    public ResourceNotFoundException( String message) {
        super("RESOURCE_NOT_FOUND",message);
    }
}
