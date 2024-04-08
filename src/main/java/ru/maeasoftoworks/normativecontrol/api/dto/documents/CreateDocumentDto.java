package ru.maeasoftoworks.normativecontrol.api.dto.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateDocumentDto {
    private String documentName;
    private String studentName;
    private MultipartFile document;
}
