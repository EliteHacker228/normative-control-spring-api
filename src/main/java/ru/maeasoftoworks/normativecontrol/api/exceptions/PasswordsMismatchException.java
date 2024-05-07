package ru.maeasoftoworks.normativecontrol.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class PasswordsMismatchException extends RuntimeException {
    public PasswordsMismatchException() {
    }

    public PasswordsMismatchException(String message) {
        super(message);
    }
}
