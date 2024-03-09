package ru.maeasoftoworks.normativecontrol.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class PatchTokenResponse {
    @Getter
    @Setter
    private String accessToken;

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
