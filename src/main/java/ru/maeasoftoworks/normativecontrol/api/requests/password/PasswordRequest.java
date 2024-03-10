package ru.maeasoftoworks.normativecontrol.api.requests.password;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PasswordRequest {
    @NotNull
    @NotBlank
    @Max(value = 255, message = "Your email is too long. Maximal length is 255")
    private String password;
}
