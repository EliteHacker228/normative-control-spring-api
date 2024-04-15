package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;

@Getter
@Setter
@ToString
public class RegisterDto {
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private Long academicGroupId;
    private Long normocontrollerId;
    private Role role;
}
