package ru.maeasoftoworks.normativecontrol.api.requests.account.password;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.utils.DateUtils;

import java.util.LinkedHashMap;


@Getter
public class PasswordResponse {
    private final String message;

    public PasswordResponse(String message) {
        this.message = message;
    }

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
