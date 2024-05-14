package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Schema(description = "Сущность пары токенов: токена доступа и токена обновления")
public class AuthJwtPair {
    @Schema(description = "Токен доступа", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJub3JtYXRpdmUtY29udHJvbC5ydSIsInN1YiI6IkkuQS5TaGFyYXBvdkB1cmZ1Lm1lIiwiaWQiOjUsInJvbGUiOiJTVFVERU5UIiwiaWF0IjoxNzE1NjI4NTU5LCJleHAiOjE3MTgyMjA1NTl9.rv41jg5LfKkI6CCHXFlf0G0MMT7_Hm4Ao2pHxMSfPHk")
    private final String accessToken;

    @Schema(description = "Токен обновления", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJub3JtYXRpdmUtY29udHJvbC5ydSIsInN1YiI6IkkuQS5TaGFyYXBvdkB1cmZ1Lm1lIiwiaWQiOjUsInJvbGUiOiJTVFVERU5UIiwiaWF0IjoxNzE1NjI4NTU5LCJleHAiOjE3MTgyMjA1NTl9.eRn_FBFdCp067Nz-xuzxnlyO91WX17NJgTNG8FClWY8")
    private final String refreshToken;
}
