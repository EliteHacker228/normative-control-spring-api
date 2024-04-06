package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.login.LoginData;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserAlreadyExistsException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserDoesNotExistsException;
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
}
