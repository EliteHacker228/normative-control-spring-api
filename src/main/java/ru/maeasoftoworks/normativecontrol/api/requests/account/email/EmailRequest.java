package ru.maeasoftoworks.normativecontrol.api.requests.account.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.validation.universityEmailValidation.UniversityEmail;

@Getter
@Setter
public class EmailRequest {
    @Email(message = "Email is incorrect")
    @UniversityEmail
    @NotNull(message = "Email can not be null")
    @NotBlank(message = "Email can not be empty")
    @Size(max = 255, message = "Your password is too long. Maximal length is 255")
    private String email;
}
