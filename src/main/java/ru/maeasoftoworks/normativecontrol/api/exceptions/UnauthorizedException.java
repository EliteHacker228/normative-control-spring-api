package ru.maeasoftoworks.normativecontrol.api.exceptions;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
