package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.validation.UniversityEmail;

@Getter
@Setter
@ToString
public class RegisterDto {
    @NotNull
    @NotEmpty
    @UniversityEmail
    @Email
    private String email;
    @NotNull
    @NotEmpty
    private String password;
    @NotNull
    @NotEmpty
    private String fullName;
    @NotNull
    private Long academicGroupId;
    private Role role;
}
