package ru.maeasoftoworks.normativecontrol.api.requests.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequest {
    @NotNull(message = "Password can not be null")
    @NotBlank(message = "Password can not be empty")
    @Size(max = 255, message = "Your password is too long. Maximal length is 255")
    private String password;
}
