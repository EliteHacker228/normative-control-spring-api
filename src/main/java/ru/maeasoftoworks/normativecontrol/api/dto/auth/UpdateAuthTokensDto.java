package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Сущность токена обновления")
public class UpdateAuthTokensDto {
    @Schema(description = "Токен обновления", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJub3JtYXRpdmUtY29udHJvbC5ydSIsInN1YiI6IkkuQS5TaGFyYXBvdkB1cmZ1Lm1lIiwiaWQiOjUsInJvbGUiOiJTVFVERU5UIiwiaWF0IjoxNzE1NjI4NTU5LCJleHAiOjE3MTgyMjA1NTl9.eRn_FBFdCp067Nz-xuzxnlyO91WX17NJgTNG8FClWY8")
    @NotEmpty(message = "refreshToken is missing")
    private String refreshToken;
}
