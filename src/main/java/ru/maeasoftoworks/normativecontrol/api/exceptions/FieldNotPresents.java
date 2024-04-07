package ru.maeasoftoworks.normativecontrol.api.exceptions;

public class FieldNotPresents extends RuntimeException {

    public FieldNotPresents() {
    }

    public FieldNotPresents(String message) {
        super(message);
    }
}
