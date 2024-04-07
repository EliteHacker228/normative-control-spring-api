package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginData {
    private String email;
    private String password;
}
