package ru.maeasoftoworks.normativecontrol.api.requests.email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.utils.DateUtils;

import java.util.LinkedHashMap;

@Getter
public class EmailResponse {
    private final String accessToken;
    private final LinkedHashMap<String, String> refreshToken = new LinkedHashMap<>();
    public EmailResponse(JwtToken accessToken, JwtToken refreshToken){
        this.accessToken = accessToken.getCompactToken();
        this.refreshToken.put("refreshToken", refreshToken.getCompactToken());
        this.refreshToken.put("createdAt", DateUtils.dateToIsoFormattedString(refreshToken.getJws().getPayload().getIssuedAt()));
        this.refreshToken.put("expiresAt", DateUtils.dateToIsoFormattedString(refreshToken.getJws().getPayload().getIssuedAt()));
    }

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}