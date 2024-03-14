package ru.maeasoftoworks.normativecontrol.api.requests.documents.authed.isVerified;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Getter
@Setter
public class IsVerifiedAuthedResponse {
    private final String message;

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
