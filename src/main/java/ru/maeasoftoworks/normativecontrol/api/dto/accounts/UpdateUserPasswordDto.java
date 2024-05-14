package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Schema(description = "Сущность для обновления пароля учётной записи")
public class UpdateUserPasswordDto {
    @Schema(description = "Старый пароль от учётной записи", example = "Leon1d0v_p4ssw0rd_8764")
    private String oldPassword;

    @Schema(description = "Новый пароль, который будет назначе учётной записи", example = "NEW_Leon1d0v_p4ssw0rd_8764")
    @NotEmpty(message = "password is missing")
    @Length(min = 8, max = 255, message = "password can not be shorter than 8 or longer than 255 characters")
    private String newPassword;
}
