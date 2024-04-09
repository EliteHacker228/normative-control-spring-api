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
    private String id;
    private String source;
    private Map<String, String> results = new HashMap<>();

    public Message(Long id, String sourceName, String docxResultName, String htmlResultName) {
        this.id = String.valueOf(id);
        this.source = sourceName;
        this.results.put("docx", docxResultName);
        this.results.put("html", htmlResultName);
    }

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
