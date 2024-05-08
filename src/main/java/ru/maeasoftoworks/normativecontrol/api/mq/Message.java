package ru.maeasoftoworks.normativecontrol.api.mq;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

@Getter
public class Message {
    private Long userId;
    private Long documentId;

    public Message(Long userId, Long documentId) {
        this.userId = userId;
        this.documentId = documentId;
    }

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
