package ru.maeasoftoworks.normativecontrol.api.dto.documents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Schema(description = "Сущность доклада об ошибке")
public class DocumentReportDto {
    @Schema(description = "ID ошибки, которая отмечается, как сомнительная", example = "m21")
    @NotEmpty(message = "mistakeId is missing")
    @Length(min = 1, max = 255, message = "mistakeId can not be shorter than 1 or longer than 255")
    private String mistakeId;
}
