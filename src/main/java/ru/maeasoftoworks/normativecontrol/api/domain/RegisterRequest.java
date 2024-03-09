package ru.maeasoftoworks.normativecontrol.api.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.validators.UniversityEmail;

@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Email(message = "Email is incorrect")
    @UniversityEmail
    @NotNull(message = "Email is null")
    @NotBlank(message = "Email is empty")
    @Getter
    @Setter
    private String email;

    @NotNull(message = "Password is null")
    @NotBlank(message = "Password is empty")
    @Getter
    @Setter
    private String password;
}
