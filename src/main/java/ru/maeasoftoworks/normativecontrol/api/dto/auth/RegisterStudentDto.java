package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.maeasoftoworks.normativecontrol.api.validation.cyrillicName.CyrillicFullName;
import ru.maeasoftoworks.normativecontrol.api.validation.universityEmail.UniversityEmail;

@Getter
@Setter
@ToString
@Schema(description = "Сущность запроса на создание учётной записи")
public class RegisterStudentDto {
    @Schema(description = "Адрес электронной почты, к которому будет привязана учётная запись", example = "t.g.leonidov@urfu.me")
    @UniversityEmail(message = "email value must be a valid e-mail address in @urfu.me, @urfu.ru or @at.urfu.ru domain")
    @Email(message = "email value must be a valid e-mail address in @urfu.me, @urfu.ru or @at.urfu.ru domain")
    @NotEmpty(message = "email is missing")
    @Length(max = 255, message = "email can not be longer than 255 characters")
    private String email;

    @Schema(description = "Пароль, по которому будет осуществляться доступ к учётной записи", example = "Leon1d0v_p4ssw0rd_8764")
    @NotEmpty(message = "password is missing")
    @Length(min = 8, max = 255, message = "password can not be shorter than 8 or longer than 255 characters")
    private String password;

    @Schema(description = "ФИО владельца учётной записи", example = "Леонидов Тимофей Георгиевич")
    @CyrillicFullName(message = "Your name must be a valid cyrillic name, with first name and last name (optional middle name), without numbers, special symbols, etc")
    @NotEmpty(message = "fullName is missing")
    @Length(min = 3, max = 255, message = "Your fullName can not be shorter than 3 or longer than 255")
    private String fullName;

    @Schema(description = "ID академической группы, к которой будет приписана учётная запись", example = "5")
    private Long academicGroupId;
}
