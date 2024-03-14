package ru.maeasoftoworks.normativecontrol.api.requests.documents.open.verification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class VerificationOpenResponse {
    private final String documentId;
    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
