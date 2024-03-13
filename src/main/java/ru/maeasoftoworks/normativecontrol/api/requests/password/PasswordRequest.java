package ru.maeasoftoworks.normativecontrol.api.requests.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PasswordRequest {
    @NotNull
    @NotBlank
    @Size(max = 255, message = "Your email is too long. Maximal length is 255")
    private String password;
}
