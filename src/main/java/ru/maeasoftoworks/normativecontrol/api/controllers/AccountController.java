package ru.maeasoftoworks.normativecontrol.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.AuthRequest;
import ru.maeasoftoworks.normativecontrol.api.domain.AuthResponse;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.repositories.AccessTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.RefreshTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.services.LoginService;
import ru.maeasoftoworks.normativecontrol.api.utils.DateUtils;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccessTokensRepository accessTokensRepository;

    @Autowired
    private RefreshTokensRepository refreshTokensRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LoginService loginService;

    @SneakyThrows
    @PostMapping("/login")
    private ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        String email = authRequest.getEmail();
        String plainTextPassword = authRequest.getPassword();

        JwtToken[] tokens = loginService.loginUser(email, plainTextPassword);

        if (tokens.length == 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Login is failed due to wrong data");

            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());
        }

        JwtToken accessToken = tokens[0];
        JwtToken refreshToken = tokens[1];
        User user = accessToken.getUser();
        AuthResponse authResponse = new AuthResponse(user, accessToken, refreshToken);
        String authResponseJson = authResponse.getAsJsonString();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authResponseJson);
    }

    @PostMapping("/register")
    private String register() {
        return "/register";
    }

    @PatchMapping("/token")
    private String token(@RequestParam(name = "refreshToken") String jwtRefreshToken) {
        return "/token";
    }

    @PatchMapping("/password")
    private String password() {
        return "/password";
    }

    @PatchMapping("/email")
    private String email() {
        return "/email";
    }

    @GetMapping("/sessions")
    private String sessions() {
        return "/sessions";
    }

    @GetMapping("/verify")
    private String getVerify() {
        return "GET /verify";
    }

    @PostMapping("/verify")
    private String PostVerify() {
        return "POST /verify";
    }

    @ExceptionHandler
    private ResponseEntity<String> handleExceptions(Exception exception) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Something went wrong on server side. Try to contact back-end developing team to get help");

        ResponseEntity<String> response = ResponseEntity
                .internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonObject.toJSONString());

        return response;
    }
}
