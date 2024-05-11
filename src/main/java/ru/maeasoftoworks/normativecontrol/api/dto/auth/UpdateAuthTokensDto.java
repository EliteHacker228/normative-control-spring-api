package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAuthTokensDto {
    @NotNull
    @NotEmpty
    private String refreshToken;
}
