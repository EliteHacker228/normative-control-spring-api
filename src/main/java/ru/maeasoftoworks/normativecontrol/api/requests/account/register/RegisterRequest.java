package ru.maeasoftoworks.normativecontrol.api.requests.account.register;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.validation.universityEmailValidation.UniversityEmail;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @Email(message = "Email is incorrect")
    @UniversityEmail
    @NotNull(message = "Email can not be null")
    @NotBlank(message = "Email can not be empty")
    private String email;

    @NotNull(message = "Password can not be null")
    @NotBlank(message = "Password can not be empty")
    @Size(max = 255, message = "Your password is too long. Maximal length is 255")
    private String password;
}
