package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;

@Getter
@Setter
@ToString
public class RegisterDto {
    @NotNull
    @NotEmpty
    private String email;
    @NotNull
    @NotEmpty
    private String password;
    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    @NotEmpty
    private String middleName;
    @NotNull
    @NotEmpty
    private String lastName;
    @NotNull
    private Long academicGroupId;
    private Role role;
}
