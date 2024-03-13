package ru.maeasoftoworks.normativecontrol.api.requests.account.token;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {
    @NotNull(message = "Refresh token can not be null")
    @NotBlank(message = "Refresh token can not be blank")
    @NotEmpty(message = "Refresh token can not be empty")
    @Getter
    @Setter
    private String refreshToken;
}
