package ru.maeasoftoworks.normativecontrol.api.requests.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.validation.universityEmailValidation.UniversityEmail;


@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "Email is incorrect")
    @UniversityEmail
    @NotNull(message = "Email can not be null")
    @NotBlank(message = "Email can not be empty")
    @Getter
    @Setter
    private String email;

    @NotNull(message = "Password can not be null")
    @NotBlank(message = "Password can not be empty")
    @Getter
    @Setter
    private String password;
}
