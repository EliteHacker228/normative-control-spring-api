package ru.maeasoftoworks.normativecontrol.api.requests.account.account.delete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class DeleteAccountResponse {
    private final String message;

    public DeleteAccountResponse(String message) {
        this.message = message;
    }

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
