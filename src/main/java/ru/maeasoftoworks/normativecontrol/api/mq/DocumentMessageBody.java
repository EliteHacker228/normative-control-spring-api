package ru.maeasoftoworks.normativecontrol.api.mq;

//{"document": "123456/source.docx","replyTo": "responces_1","resultDocx": "123456/result.docx","resultHtml": "123456/result.docx"};

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

public class DocumentMessageBody {

    @JsonIgnore
    @Getter
    private final String correlationId;
    @JsonIgnore
    @Getter
    private final String fileName;

    @Getter
    private final String document;
    @Getter
    private final String replyTo;
    @Getter
    private final String resultDocx;
    @Getter
    private final String resultHtml;

    public DocumentMessageBody(String correlationId, String fileName, String replyTo) {
        this.correlationId = correlationId;
        this.fileName = fileName;

        this.document = correlationId + "/" + fileName;
        this.replyTo = replyTo;
        this.resultDocx = correlationId + "/result.docx";
        this.resultHtml = correlationId + "/result.html";
    }

    @SneakyThrows
    @JsonIgnore
    public String getAsJsonString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
