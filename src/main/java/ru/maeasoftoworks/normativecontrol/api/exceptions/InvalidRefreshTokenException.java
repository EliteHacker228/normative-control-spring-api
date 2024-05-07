package ru.maeasoftoworks.normativecontrol.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
