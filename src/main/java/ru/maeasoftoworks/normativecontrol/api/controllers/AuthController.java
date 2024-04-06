package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.UpdateAuthTokensDto;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.login.LoginData;
import ru.maeasoftoworks.normativecontrol.api.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    private AuthJwtPair login(@RequestBody LoginData loginData) {
        return authService.login(loginData);
    }

    @PostMapping(value = "/register/student", produces = MediaType.APPLICATION_JSON_VALUE)
    private AuthJwtPair registerStudent(@RequestBody Student student) {
        return authService.register(student);
    }

    @PostMapping(value = "/register/normocontroller", produces = MediaType.APPLICATION_JSON_VALUE)
    private AuthJwtPair registerNormocontroller(@RequestBody Normocontroller normocontroller) {
        return authService.register(normocontroller);
    }

    @PutMapping(value = "/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    private AuthJwtPair updateAuthTokens(@RequestBody UpdateAuthTokensDto updateAuthTokensDto) {
        String refreshToken = updateAuthTokensDto.getRefreshToken();
        return authService.updateAuthTokens(refreshToken);
    }
}
