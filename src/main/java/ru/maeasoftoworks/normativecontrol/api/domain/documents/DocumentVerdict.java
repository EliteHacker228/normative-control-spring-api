package ru.maeasoftoworks.normativecontrol.api.domain.documents;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum DocumentVerdict {
    NOT_CHECKED("Работа не проверена"), ACCEPTED("Работа принята"), REJECTED("Работа отклонена");

    private final String russianAlias;

    DocumentVerdict(String russianAlias) {
        this.russianAlias = russianAlias;
    }

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
