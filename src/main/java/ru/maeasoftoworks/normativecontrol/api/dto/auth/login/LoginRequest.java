package ru.maeasoftoworks.normativecontrol.api.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.WebDto;

@AllArgsConstructor
@Getter
@Setter
public class LoginRequest extends WebDto {
    private String email;
    private String password;
}
