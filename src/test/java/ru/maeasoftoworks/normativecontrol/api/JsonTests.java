package ru.maeasoftoworks.normativecontrol.api;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.maeasoftoworks.normativecontrol.api.requests.account.login.LoginResponse;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;

import java.util.LinkedHashMap;

public class JsonTests {

    @SneakyThrows
    @Test
    public void jsonTests(){
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJub3JtYXRpdmUtY29udHJvbC5ydSIsImlzcyI6Im5vcm1hdGl2ZS1jb250cm9sLnJ1IiwiaWF0IjoxNzA5NzU3MzQ5LCJzdWIiOiJpbnNwZWN0b3IwMSIsInJvbGUiOiJbXCJJTlNQRUNUT1JcIl0iLCJlbnQiOjYxOTY0MjA3NSwiZXhwIjoxNzQxMjkzMzQ5fQ.2gcEb25PTbxvbedbrmXlUn1M2qA0KxpK08Z5rCvdVHQ");
        LinkedHashMap<String, String> refreshToken = new LinkedHashMap<>();
        refreshToken.put("refreshToken", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJub3JtYXRpdmUtY29udHJvbC5ydSIsImlzcyI6Im5vcm1hdGl2ZS1jb250cm9sLnJ1IiwiaWF0IjoxNzA5NzU3MzQ5LCJzdWIiOiJpbnNwZWN0b3IwMSIsInJvbGUiOiJbXCJJTlNQRUNUT1JcIl0iLCJlbnQiOjYxOTY0MjA3NSwiZXhwIjoxNzQxMjkzMzQ5fQ.2gcEb25PTbxvbedbrmXlUn1M2qA0KxpK08Z5rCvdVHQ");
        refreshToken.put("createdAt", "2024-03-06T20:35:49.923524200Z");
        refreshToken.put("expiresAt", "2025-03-06T20:35:49.923524200Z");
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setCredentialsVerified(true);
        loginResponse.setRole(Role.ADMIN);
        loginResponse.setTokenType("Bearer");

        String result = loginResponse.getAsJsonString();
        System.out.println(result);
    }
}
