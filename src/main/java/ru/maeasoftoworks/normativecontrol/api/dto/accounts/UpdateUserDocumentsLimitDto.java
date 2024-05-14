package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Сущность обновления лимита документов студента")
public class UpdateUserDocumentsLimitDto {
    @Schema(description = "Значение лимита документов студента")
    @Min(value = 1, message = "documentsLimit can't be less than 1")
    @Max(value = 30, message = "documentsLimit can't be more than 30")
    private int documentsLimit;
}
