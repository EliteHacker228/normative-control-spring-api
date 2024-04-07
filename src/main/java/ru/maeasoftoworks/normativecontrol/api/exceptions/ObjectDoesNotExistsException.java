package ru.maeasoftoworks.normativecontrol.api.exceptions;

public class ObjectDoesNotExistsException extends RuntimeException {

    public ObjectDoesNotExistsException() {
    }

    public ObjectDoesNotExistsException(String message) {
        super(message);
    }
}
