package ru.maeasoftoworks.normativecontrol.api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.RegisterDto;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.UpdateAuthTokensDto;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.LoginData;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserAlreadyExistsException;
import ru.maeasoftoworks.normativecontrol.api.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    private AuthJwtPair login(@RequestBody @Valid LoginData loginData) {
        return authService.login(loginData);
    }

    @PostMapping("/register/student")
    private ResponseEntity registerStudent(@RequestBody RegisterDto registerDto) {
        try {
            registerDto.setRole(Role.STUDENT);
            AuthJwtPair authJwtPair = authService.register(registerDto);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(authJwtPair);
        } catch (UserAlreadyExistsException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Email " + registerDto.getEmail() + " is already in use");
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(jsonObject.toJSONString());
        }
    }

    @PostMapping("/register/normocontroller")
    private ResponseEntity registerNormocontroller(@RequestBody RegisterDto registerDto) {
        try {
            registerDto.setRole(Role.NORMOCONTROLLER);
            authService.register(registerDto);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Normocontroller " + registerDto.getEmail() + " registered successfully");
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jsonObject.toJSONString());
        } catch (UserAlreadyExistsException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Email " + registerDto.getEmail() + " is already in use");
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(jsonObject.toJSONString());
        }
    }

    @PutMapping("/tokens")
    private AuthJwtPair updateAuthTokens(@RequestBody UpdateAuthTokensDto updateAuthTokensDto) {
        String refreshToken = updateAuthTokensDto.getRefreshToken();
        return authService.updateAuthTokens(refreshToken);
    }
}
