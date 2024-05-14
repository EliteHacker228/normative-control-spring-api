package ru.maeasoftoworks.normativecontrol.api.dto.documents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;
import ru.maeasoftoworks.normativecontrol.api.validation.cyrillicName.CyrillicFullName;

@Getter
@Setter
@Schema(description = "Сущность загрузки документа на проверку")
public class CreateDocumentDto {
    @Schema(description = "Имя документа. Может быть в формате *ИМЯ_ДОКУМЕНТА*.docx или *ИМЯ_ДОКУМЕНТА*", example = "А.П.Вячесловов РИ-400012 диплом.docx")
    @NotEmpty(message = "documentName is missing")
    private String documentName;

//    @Schema(description = "Имя студента. Должно быть валидным ФИО, состоящим из имени, фамилии (опционально - отчества), записанными кириллицей.")
//    @NotEmpty(message = "studentName can not be empty")
//    @CyrillicFullName(message = "studentName be a valid cyrillic full name, with first name and last name (optional middle name), without numbers, special symbols, etc")
//    @NotEmpty(message = "studentName is missing")
//    @Length(min = 3, max = 255, message = "studentName can not be shorter than 3 or longer than 255")
//    private String studentName;

//    @Schema(description = "ID академической группы студента")
//    @NotNull(message = "academicGroupId is missing")
//    private Long academicGroupId;

    @Schema(description = "Документ в формате .docx")
    @NotNull(message = "document is missing")
    private MultipartFile document;
}
