package ru.maeasoftoworks.normativecontrol.api.exceptions;

public class ResourceDoesNotExists extends RuntimeException {

    public ResourceDoesNotExists() {
    }

    public ResourceDoesNotExists(String message) {
        super(message);
    }
}
