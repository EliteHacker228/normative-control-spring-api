package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.maeasoftoworks.normativecontrol.api.validation.universityEmail.UniversityEmail;

@Getter
@Setter
@Schema(description = "Сущность запроса на вход в учётную запись")
public class LoginData {
    @Schema(description = "Адрес электронной почты существующей учётной записи", example = "user.email@urfu.me")
    @UniversityEmail(message = "email value must be a valid e-mail address in @urfu.me, @urfu.ru or @at.urfu.ru domain")
    @Email(message = "email value must be a valid e-mail address in @urfu.me, @urfu.ru or @at.urfu.ru domain")
    @NotEmpty(message = "email is missing")
    @Length(max = 255, message = "email can not be longer than 255 characters")
    private String email;

    @Schema(description = "Пароль существующей учётной записи", example = "user_password")
    @NotEmpty(message = "password is missing")
    @Length(min = 8, max = 255, message = "password can not be shorter than 8 or longer than 255 characters")
    private String password;
}
