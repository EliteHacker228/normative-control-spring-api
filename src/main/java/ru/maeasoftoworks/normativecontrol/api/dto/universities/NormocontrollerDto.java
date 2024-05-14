package ru.maeasoftoworks.normativecontrol.api.dto.universities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.maeasoftoworks.normativecontrol.api.validation.cyrillicName.CyrillicFullName;

@Getter
@Setter
@Builder
@Schema(description = "Сущность создания нормоконтролёра")
public class NormocontrollerDto {

    private Long id;

    @Schema(description = "ФИО нормоконтролёра", example = "Шведов Альберт Михайлович")
    @CyrillicFullName(message = "Your name must be a valid cyrillic name, with first name and last name (optional middle name), without numbers, special symbols, etc")
    @NotEmpty(message = "fullName is missing")
    @Length(min = 3, max = 255, message = "Your fullName can not be shorter than 3 or longer than 255")
    private String fullName;
}
