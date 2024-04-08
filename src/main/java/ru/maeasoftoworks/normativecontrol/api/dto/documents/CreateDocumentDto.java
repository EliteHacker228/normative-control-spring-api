package ru.maeasoftoworks.normativecontrol.api.dto.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.AcademicGroup;

@Getter
@Setter
public class CreateDocumentDto {
    private String documentName;
    private String studentName;
    private Long academicGroupId;
    private MultipartFile document;
}
