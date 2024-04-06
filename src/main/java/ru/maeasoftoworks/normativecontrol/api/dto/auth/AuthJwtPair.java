package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AuthJwtPair {
    private final String accessToken;
    private final String refreshToken;
}
