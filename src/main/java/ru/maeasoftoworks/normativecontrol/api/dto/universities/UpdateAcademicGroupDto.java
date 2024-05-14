package ru.maeasoftoworks.normativecontrol.api.dto.universities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Schema(description = "Сущность изменения академической группы")
public class UpdateAcademicGroupDto {
    @Schema(description = "Имя, которое будет приписано академической группу")
    @NotEmpty(message = "Academic group name can not be empty")
    @Length(min = 1, max = 255, message = "Academic group length can not be less than 1 or bigger than 255")
    private String name;
    @Schema(description = "ID нормоконтролёра, к которому будет приписана группа")
    private Long normocontrollerId;
}
