package ru.maeasoftoworks.normativecontrol.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
public class JwtTests {
    @Autowired
    JwtUtils jwtUtils;

    @Value("${jwt.refreshToken.key}")
    private String REFRESH_TOKEN_KEY;

    @Test
    public void jwtUtilsTest() {
        User user = new User("Kuznetsov.Mikhail@urfu.me", "Кузнецов М.А.", "misha.kuznetsov", "ilovepuppies2548", Role.STUDENT, "UrFU");

        JwtToken userAccessToken = jwtUtils.generateAccessTokenForUser(user);
        JwtToken userRefreshToken = jwtUtils.generateRefreshTokenForUser(user);
        Assertions.assertTrue(jwtUtils.isAccessTokenReadable(userAccessToken.getCompactToken()));
        Assertions.assertTrue(jwtUtils.isRefreshTokenReadable(userRefreshToken.getCompactToken()));


        String invalidAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJub3JtYXRpdmUtY29udHJvbC5ydSIsImlzcyI6Im5vcm1hdGl2ZS1jb250cm9sLnJ1IiwiaWF0IjoxNzA5ODM3Nzk3LCJzdWIiOiJtaXNoYS5rdXpuZXRzb3YiLCJyb2xlIjpbIlNUVURFTlQiXSwiZW50IjoxNzQxMzczNzk3Nzg3LCJleHAiOjE3MDk4Mzg2OTd9.abiRQRqrpwylDF5ECKK8tKZwlGwWhuzHVLXJSS0A7j4";
        String invalidRefreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJub3JtYXRpdmUtY29udHJvbC5ydSIsImlzcyI6Im5vcm1hdGl2ZS1jb250cm9sLnJ1IiwiaWF0IjoxNzA5ODM3Nzk3LCJzdWIiOiJtaXNoYS5rdXpuZXRzb3YiLCJyb2xlIjpbIlNUVURFTlQiXSwiZW50IjoxNzQxMzczNzk3OTQwLCJleHAiOjE3MDk4NDEzOTd9.abXKpM4Ol0dEh-AhHIl_eNXUHqsWvFG8gKAWghv7vek";
        Assertions.assertFalse(jwtUtils.isAccessTokenReadable(invalidAccessToken));
        Assertions.assertFalse(jwtUtils.isRefreshTokenReadable(invalidRefreshToken));
    }
}
