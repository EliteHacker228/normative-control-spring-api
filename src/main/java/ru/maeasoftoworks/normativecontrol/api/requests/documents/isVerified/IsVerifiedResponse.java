package ru.maeasoftoworks.normativecontrol.api.requests.documents.isVerified;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class IsVerifiedResponse {
    private final String message;

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
