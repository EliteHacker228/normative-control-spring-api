package ru.maeasoftoworks.normativecontrol.api.dto.universities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Schema(description = "Сущность создания академической группы")
public class CreateAcademicGroupDto {
    @Schema(description = "Имя создаваемой академической группу")
    @NotEmpty(message = "Academic group name can not be empty")
    @Length(min = 1, max = 255, message = "Academic group length can not be less than 1 or bigger than 255")
    private String name;

    @Schema(description = "ID нормоконтролера создаваемой академической групы (опционально)")
    private Long normocontrollerId;
}
