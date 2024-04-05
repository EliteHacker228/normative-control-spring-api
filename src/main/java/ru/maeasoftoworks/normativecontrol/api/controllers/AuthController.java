package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.login.LoginRequest;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.login.LoginResponse;
import ru.maeasoftoworks.normativecontrol.api.services.AuthService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    private LoginResponse login(@RequestBody LoginRequest loginRequest){
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Jwt[] jwts = authService.loginByCredentials(email, password);
        String compactAccessToken = jwts[0].getCompactToken();
        String compactRefreshToken = jwts[1].getCompactToken();
        return new LoginResponse(compactAccessToken, compactRefreshToken);
    }
}
