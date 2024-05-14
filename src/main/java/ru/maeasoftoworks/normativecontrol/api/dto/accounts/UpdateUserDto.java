package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.maeasoftoworks.normativecontrol.api.validation.cyrillicName.CyrillicFullName;

@Getter
@Setter
@Schema(description = "Сущность обновления данных о пользователе")
public class UpdateUserDto {
    @Schema(description = "ФИО владельца учётной записи", example = "Столяров Михаил Владимирович")
    @CyrillicFullName(message = "Your name must be a valid cyrillic name, with first name and last name (optional middle name), without numbers, special symbols, etc")
    @NotEmpty(message = "fullName is missing")
    @Length(min = 3, max = 255, message = "Your fullName can not be shorter than 3 or longer than 255")
    private String fullName;

    private Long academicGroupId;
}
