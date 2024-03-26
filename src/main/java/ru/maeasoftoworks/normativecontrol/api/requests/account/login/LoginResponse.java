package ru.maeasoftoworks.normativecontrol.api.requests.account.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.utils.DateUtils;

import java.util.LinkedHashMap;
import java.util.List;

public class LoginResponse {
    @Getter
    @Setter
    private String accessToken;
    @Getter
    @Setter
    private LinkedHashMap<String, String> refreshToken;
    @Getter
    @Setter
    private boolean isCredentialsVerified;
    @Getter
    @Setter
    private Role role;
    @Getter
    @Setter
    private String tokenType;

    public LoginResponse() {

    }

    public LoginResponse(JwtToken accessToken, JwtToken refreshToken) {
        User user = accessToken.getUser();
        boolean isCredentialsVerified = user.getIsVerified();
        Role role = user.getRole();
        final String tokenType = "Bearer";
        LinkedHashMap<String, String> refreshTokenMap = new LinkedHashMap<>();
        refreshTokenMap.put("refreshToken", refreshToken.getCompactToken());
        refreshTokenMap.put("createdAt", DateUtils.dateToIsoFormattedString(refreshToken.getJws().getPayload().getIssuedAt()));
        refreshTokenMap.put("expiresAt", DateUtils.dateToIsoFormattedString(refreshToken.getJws().getPayload().getExpiration()));

        this.setAccessToken(accessToken.getCompactToken());
        this.setRefreshToken(refreshTokenMap);
        this.setCredentialsVerified(isCredentialsVerified);
        this.setRole(role);
        this.setTokenType(tokenType);
    }

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
