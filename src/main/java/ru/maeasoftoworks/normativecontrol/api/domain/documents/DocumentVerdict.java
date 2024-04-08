package ru.maeasoftoworks.normativecontrol.api.domain.documents;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DocumentVerdict {
    NOT_CHECKED, ACCEPTED, REJECTED;

    @JsonCreator
    public static DocumentVerdict fromText(String text) {
        for (DocumentVerdict r : DocumentVerdict.values()) {
            if (r.name().equals(text)) {
                return r;
            }
        }
        throw new IllegalArgumentException();
    }
}
