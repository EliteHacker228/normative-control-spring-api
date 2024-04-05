package ru.maeasoftoworks.normativecontrol.api.dto.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class WebDto {
    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
