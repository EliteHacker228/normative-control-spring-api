package ru.maeasoftoworks.normativecontrol.api.exceptions;

public class AccessTokenRefreshFailedException extends RuntimeException {
    public AccessTokenRefreshFailedException() {
        super();
    }

    public AccessTokenRefreshFailedException(String message) {
        super(message);
    }
}
