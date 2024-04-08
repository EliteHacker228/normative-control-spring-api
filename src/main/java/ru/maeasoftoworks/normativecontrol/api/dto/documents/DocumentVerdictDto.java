package ru.maeasoftoworks.normativecontrol.api.dto.documents;

import lombok.Getter;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.DocumentVerdict;

@Getter
@Setter
public class DocumentVerdictDto {
    private DocumentVerdict verdict;
    private String message;
}
