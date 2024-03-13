package ru.maeasoftoworks.normativecontrol.api.requests.documents.verification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class VerificationResponse {
    private final String documentId;
    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
