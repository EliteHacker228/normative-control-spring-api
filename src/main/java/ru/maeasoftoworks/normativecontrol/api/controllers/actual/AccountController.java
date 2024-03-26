package ru.maeasoftoworks.normativecontrol.api.controllers.actual;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.requests.account.register.RegisterRequest;
import ru.maeasoftoworks.normativecontrol.api.requests.account.register.RegisterResponse;
import ru.maeasoftoworks.normativecontrol.api.services.AccountService;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    private RegisterResponse register(@Valid @RequestBody RegisterRequest request){
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
}
