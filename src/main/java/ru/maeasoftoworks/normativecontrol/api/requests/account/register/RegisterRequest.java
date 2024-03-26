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

    @NotNull(message = "First name can not be null")
    @NotBlank(message = "First name can not be empty")
    @Size(max = 255, message = "Your first name is too long. Maximal length is 255")
    private String firstName;

    @NotNull(message = "Middle name can not be null")
    @NotBlank(message = "Middle name can not be empty")
    @Size(max = 255, message = "Your middle name is too long. Maximal length is 255")
    private String middleName;

    @NotNull(message = "Last name can not be null")
    @NotBlank(message = "Last name can not be empty")
    @Size(max = 255, message = "Your last name is too long. Maximal length is 255")
    private String lastName;

    @NotNull(message = "Academic group can not be null")
    @NotBlank(message = "Academic group name can not be empty")
    @Size(max = 255, message = "Your academic group is too long. Maximal length is 255")
    private String academicGroup;

    @NotNull(message = "Organization group can not be null")
    @NotBlank(message = "Organization group name can not be empty")
    @Size(max = 255, message = "Your organization group is too long. Maximal length is 255")
    private String organization;
}
