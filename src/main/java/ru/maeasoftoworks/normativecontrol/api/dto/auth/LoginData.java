package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.maeasoftoworks.normativecontrol.api.validation.UniversityEmail;

@Getter
@Setter
public class LoginData {
    @NotEmpty
    @NotNull
    @UniversityEmail
    @Email
    private String email;
    @NotEmpty
    @NotNull
    @Length(min = 8)
    private String password;
}
