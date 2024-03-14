package ru.maeasoftoworks.normativecontrol.api.requests.documents.authed.verification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Getter
@Setter
public class VerificationAuthedResponse {
    private final String documentId;
    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
