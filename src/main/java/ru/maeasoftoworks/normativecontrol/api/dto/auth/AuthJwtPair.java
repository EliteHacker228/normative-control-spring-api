package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.maeasoftoworks.normativecontrol.api.dto.JsonSerializable;

@RequiredArgsConstructor
@Getter
public class AuthJwtPair extends JsonSerializable {
    private final String accessToken;
    private final String refreshToken;
}
