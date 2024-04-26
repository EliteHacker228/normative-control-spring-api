package ru.maeasoftoworks.normativecontrol.api.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
