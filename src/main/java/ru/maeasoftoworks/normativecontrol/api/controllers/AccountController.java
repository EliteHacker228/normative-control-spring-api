package ru.maeasoftoworks.normativecontrol.api.controllers;

import jakarta.validation.Valid;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.*;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserAlreadyExistsException;
import ru.maeasoftoworks.normativecontrol.api.repositories.AccessTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.RefreshTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.services.AccountService;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

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
    private AccountService accountService;

    @PostMapping("/login")
    private ResponseEntity<String> login(@Valid @RequestBody AuthRequest authRequest) {
        String email = authRequest.getEmail();
        String plainTextPassword = authRequest.getPassword();

        // Throws unchecked WrongCredentialsException handled by @ExceptionHandler
        JwtToken[] tokens = accountService.loginUserByCreds(email, plainTextPassword);

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
    private ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String plainTextPassword = registerRequest.getPassword();

        // Throws unchecked UserAlreadyExistsException handled by @ExceptionHandler
        JwtToken[] tokens = accountService.registrateUserByCreds(email, plainTextPassword);

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

    @PatchMapping("/token")
    private ResponseEntity<String> token(@RequestBody PatchTokenRequest patchTokenRequest) {
        JwtToken newAccessToken = accountService.updateAccessTokenByRefreshToken(patchTokenRequest.getRefreshToken());
        PatchTokenResponse patchTokenResponse = new PatchTokenResponse(newAccessToken.getCompactToken());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(patchTokenResponse.getAsJsonString());
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
        ResponseEntity<String> responseEntity;

        switch (exception.getClass().getSimpleName()) {
            case "MethodArgumentNotValidException":
                MethodArgumentNotValidException currException = (MethodArgumentNotValidException) exception;
                StringBuilder resultMessage = new StringBuilder();
                resultMessage.append("Auth is failed: ");
                for (FieldError fieldError : currException.getFieldErrors()) {
                    resultMessage.append(fieldError.getDefaultMessage()).append(". ");
                }
                jsonObject.put("message", resultMessage.toString());
                responseEntity = ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonObject.toJSONString());
                break;
            case "UserAlreadyExistsException":
                jsonObject.put("message", "User with such email or login already exists");
                responseEntity = ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonObject.toJSONString());
                break;
            case "WrongCredentialsException":
                jsonObject.put("message", "You're trying to log-in or register with wrong credentials");
                responseEntity = ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonObject.toJSONString());
                break;
            case "AccessTokenRefreshFailedException":
                jsonObject.put("message", exception.getMessage());
                responseEntity = ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonObject.toJSONString());
                break;
            default:
                jsonObject.put("message", "Something went wrong on server side. Try to contact back-end developing team to get help");
                responseEntity = ResponseEntity
                        .internalServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonObject.toJSONString());
                break;
        }

        return responseEntity;
    }
}
