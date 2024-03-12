package ru.maeasoftoworks.normativecontrol.api.requests.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class PasswordRequest {
    @NotNull
    @NotBlank
    @Length(max = 255, message = "Your email is too long. Maximal length is 255")
    private String password;
}
