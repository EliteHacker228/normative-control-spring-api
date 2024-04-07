package ru.maeasoftoworks.normativecontrol.api.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.LoginData;
import ru.maeasoftoworks.normativecontrol.api.exceptions.*;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final JwtService jwtService;

    public AuthJwtPair login(LoginData loginData) {
        User user = usersRepository.findUserByEmail(loginData.getEmail());
        if (user == null)
            throw new UserDoesNotExistsException("User with e-mail " + loginData.getEmail() + " does not exists");
        if (!user.getPassword().equals(loginData.getPassword()))
            throw new WrongPasswordException("Given password is incorrect");

        return new AuthJwtPair(jwtService.generateAccessTokenForUser(user).getCompactToken(),
                jwtService.generateRefreshTokenForUser(user).getCompactToken());
    }

    public AuthJwtPair register(User user) {
        if (usersRepository.existsUserByEmail(user.getEmail()))
            throw new UserAlreadyExistsException("User with e-mail " + user.getEmail() + " already exists");
        usersRepository.save(user);
        return new AuthJwtPair(jwtService.generateAccessTokenForUser(user).getCompactToken(),
                jwtService.generateRefreshTokenForUser(user).getCompactToken());
    }

    public AuthJwtPair updateAuthTokens(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken))
            throw new InvalidRefreshTokenException("Given refresh token is incorrect or outdated");
        Claims jwtBody = jwtService.getClaimsFromRefreshTokenString(refreshToken).getPayload();
        String userEmail = jwtBody.getSubject();
        User user = usersRepository.findUserByEmail(userEmail);
        return new AuthJwtPair(jwtService.generateAccessTokenForUser(user).getCompactToken(),
                jwtService.generateRefreshTokenForUser(user).getCompactToken());
    }
}
