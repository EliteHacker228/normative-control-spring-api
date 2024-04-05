package ru.maeasoftoworks.normativecontrol.api.dto.auth.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.WebDto;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

@RequiredArgsConstructor
@Getter
public class LoginResponse extends WebDto {
    private final String accessToken;
    private final String refreshToken;
}
