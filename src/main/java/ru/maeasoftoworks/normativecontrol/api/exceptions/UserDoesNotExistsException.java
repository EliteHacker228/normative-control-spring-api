package ru.maeasoftoworks.normativecontrol.api.exceptions;

public class UserDoesNotExistsException extends RuntimeException {

    public UserDoesNotExistsException() {
    }

    public UserDoesNotExistsException(String message) {
        super(message);
    }
}
