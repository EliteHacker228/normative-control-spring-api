package ru.maeasoftoworks.normativecontrol.api.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.*;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.exceptions.AccessTokenRefreshFailedException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserAlreadyExistsException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.WrongCredentialsException;
import ru.maeasoftoworks.normativecontrol.api.requests.login.LoginRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.login.LoginResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.register.RegisterRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.register.RegisterResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.token.TokenRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.token.TokenResponse;
import ru.maeasoftoworks.normativecontrol.api.services.AccountService;
import ru.maeasoftoworks.normativecontrol.api.utils.HashingUtils;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
@Slf4j
public class AccountController {

    private AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    private ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String plainTextPassword = loginRequest.getPassword();
        String bcryptedPassword = passwordEncoder.encode(loginRequest.getPassword());

        // Throws unchecked WrongCredentialsException handled by @ExceptionHandler
        JwtToken[] tokens = accountService.loginUserByCreds(email, plainTextPassword);

        JwtToken accessToken = tokens[0];
        JwtToken refreshToken = tokens[1];
        User user = accessToken.getUser();
        LoginResponse loginResponse = new LoginResponse(user, accessToken, refreshToken);
        String authResponseJson = loginResponse.getAsJsonString();

        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                email,
                plainTextPassword
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info(authentication.toString());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authResponseJson);
    }

    @PostMapping("/register")
    private ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String plainTextPassword = registerRequest.getPassword();
        String bcryptedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Throws unchecked UserAlreadyExistsException handled by @ExceptionHandler
        JwtToken[] tokens = accountService.registrateUserByCreds(email, bcryptedPassword);

        JwtToken accessToken = tokens[0];
        JwtToken refreshToken = tokens[1];
        User user = accessToken.getUser();
        RegisterResponse registerResponse = new RegisterResponse(user, accessToken, refreshToken);
        String authResponseJson = registerResponse.getAsJsonString();

        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                email,
                plainTextPassword
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info(authentication.toString());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authResponseJson);
    }

    @PatchMapping("/token")
    private ResponseEntity<String> token(@RequestBody TokenRequest tokenRequest) {
        // Throws unchecked exceptions
        JwtToken[] newAccessAndRefreshToken = accountService.updateAccessTokenByRefreshToken(tokenRequest.getRefreshToken());
        TokenResponse tokenResponse = new TokenResponse(newAccessAndRefreshToken[0], newAccessAndRefreshToken[1]);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tokenResponse.getAsJsonString());
    }

    @PatchMapping("/password")
    private String password(@RequestBody String password) {
        log.info("Password controller!!!!!!!!!");
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());

        return password;
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

        if (exception instanceof MethodArgumentNotValidException) {
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
        } else if (exception instanceof UserAlreadyExistsException) {
            jsonObject.put("message", "User with such email or login already exists");
            responseEntity = ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());
        } else if (exception instanceof WrongCredentialsException) {
            jsonObject.put("message", "You're trying to log-in or register with wrong credentials");
            responseEntity = ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());
        } else if (exception instanceof AccessTokenRefreshFailedException) {
            jsonObject.put("message", exception.getMessage());
            responseEntity = ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());
        } else {
            jsonObject.put("message", "Something went wrong on server side. Try to contact back-end developing team to get help");
            responseEntity = ResponseEntity
                    .internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toJSONString());
        }

        return responseEntity;
    }
}
