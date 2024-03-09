package ru.maeasoftoworks.normativecontrol.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class PatchTokenRequest {
    @Getter
    @Setter
    private String refreshToken;
}
