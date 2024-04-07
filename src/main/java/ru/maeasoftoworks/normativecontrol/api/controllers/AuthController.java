package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.UpdateAuthTokensDto;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.LoginData;
import ru.maeasoftoworks.normativecontrol.api.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    private AuthJwtPair login(@RequestBody LoginData loginData) {
        return authService.login(loginData);
    }

    @PostMapping("/register/student")
    private AuthJwtPair registerStudent(@RequestBody Student student) {
        return authService.register(student);
    }

    @PostMapping("/register/normocontroller")
    private AuthJwtPair registerNormocontroller(@RequestBody Normocontroller normocontroller) {
        return authService.register(normocontroller);
    }

    @PutMapping("/tokens")
    private AuthJwtPair updateAuthTokens(@RequestBody UpdateAuthTokensDto updateAuthTokensDto) {
        String refreshToken = updateAuthTokensDto.getRefreshToken();
        return authService.updateAuthTokens(refreshToken);
    }
}
