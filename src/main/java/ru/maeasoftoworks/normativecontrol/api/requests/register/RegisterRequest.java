package ru.maeasoftoworks.normativecontrol.api.requests.register;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.validation.universityEmailValidation.UniversityEmail;

@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Email(message = "Email is incorrect")
    @UniversityEmail
    @NotNull(message = "Email is null")
    @NotBlank(message = "Email is empty")
    @Max(value = 255, message = "Your email is too long. Maximal length is 255")
    @Getter
    @Setter
    private String email;

    @NotNull(message = "Password is null")
    @NotBlank(message = "Password is empty")
    @Max(value = 255, message = "Your password is too long. Maximal length is 255")
    @Getter
    @Setter
    private String password;
}
