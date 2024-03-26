package ru.maeasoftoworks.normativecontrol.api.controllers.actual;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.requests.account.account.delete.DeleteAccountResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.account.account.get.AccountResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.account.account.patch.PatchAccountRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.account.email.EmailRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.account.login.LoginRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.account.login.LoginResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.account.password.PasswordRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.account.password.PasswordResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.account.register.RegisterRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.account.register.RegisterResponse;
import ru.maeasoftoworks.normativecontrol.api.requests.account.token.TokenRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.account.token.TokenResponse;
import ru.maeasoftoworks.normativecontrol.api.services.AccountService;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    private RegisterResponse postRegister(@Valid @RequestBody RegisterRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setAcademicGroup(request.getAcademicGroup());
        user.setOrganization(request.getOrganization());
        user.setRole(Role.STUDENT);

        JwtToken[] jwtTokens = accountService.registerUser(user);
        JwtToken accessToken = jwtTokens[0];
        JwtToken refreshToken = jwtTokens[1];
        return new RegisterResponse(user, accessToken, refreshToken);
    }

    @PostMapping("/login")
    private LoginResponse postLogin(@Valid @RequestBody LoginRequest request){
        String email = request.getEmail();
        String password = request.getPassword();

        JwtToken[] jwtTokens = accountService.loginUserByCreds(email, password);
        JwtToken accessToken = jwtTokens[0];
        JwtToken refreshToken = jwtTokens[1];
        return new LoginResponse(accessToken, refreshToken);
    }

    @PutMapping("/token")
    private TokenResponse putToken(@Valid @RequestBody TokenRequest request){
        JwtToken[] tokens = accountService.updateAccessTokenByRefreshToken(request.getRefreshToken());
        JwtToken accessToken = tokens[0];
        JwtToken refreshToken = tokens[1];
        return new TokenResponse(accessToken, refreshToken);
    }

    @PutMapping("/password")
    private PasswordResponse putPassword(@RequestHeader("Authorization") String bearerToken ,@Valid @RequestBody PasswordRequest request){
        String accessToken = bearerToken.substring(("Bearer ").length());
        accountService.setPasswordForUserByAccessToken(accessToken, request.getPassword());
        return new PasswordResponse("Password updated successfully");
    }

    @PutMapping("/email")
    private PasswordResponse putEmail(@RequestHeader("Authorization") String bearerToken ,@Valid @RequestBody EmailRequest request){
        String accessToken = bearerToken.substring(("Bearer ").length());
        accountService.setEmailForUserByAccessToken(accessToken, request.getEmail());
        return new PasswordResponse("Email updated successfully");
    }

    @GetMapping("")
    private AccountResponse getAccount(@RequestHeader("Authorization") String bearerToken){
        String accessToken = bearerToken.substring(("Bearer ").length());
        User user = accountService.getUserByJwtAccessToken(accessToken);
        return new AccountResponse(user);
    }

    @DeleteMapping("")
    private DeleteAccountResponse deleteAccount(@RequestHeader("Authorization") String bearerToken){
        String accessToken = bearerToken.substring(("Bearer ").length());
        accountService.deleteUserByAccessToken(accessToken);
        return new DeleteAccountResponse("User deleted successfully");
    }
}
