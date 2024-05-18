package ru.maeasoftoworks.normativecontrol.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnknownDocumentTypeException extends RuntimeException{
    public UnknownDocumentTypeException() {
    }

    public UnknownDocumentTypeException(String message) {
        super(message);
    }
}
