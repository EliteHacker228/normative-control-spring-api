package ru.maeasoftoworks.normativecontrol.api.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.login.LoginData;
import ru.maeasoftoworks.normativecontrol.api.exceptions.InvalidRefreshTokenException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserAlreadyExistsException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserDoesNotExistsException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.WrongPasswordException;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.JwtUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final JwtUtils jwtUtils;

    public AuthJwtPair login(LoginData loginData) {
        User user = usersRepository.findUserByEmail(loginData.getEmail());
        if (user == null)
            throw new UserDoesNotExistsException("User with e-mail " + loginData.getEmail() + " is not exists");
        if (!user.getPassword().equals(loginData.getPassword()))
            throw new WrongPasswordException("Given password is incorrect");

        return new AuthJwtPair(jwtUtils.generateAccessTokenForUser(user).getCompactToken(),
                jwtUtils.generateRefreshTokenForUser(user).getCompactToken());
    }

    public AuthJwtPair register(User user) {
        if (usersRepository.existsUserByEmail(user.getEmail()))
            throw new UserAlreadyExistsException("User with e-mail " + user.getEmail() + " already exists");
        usersRepository.save(user);
        return new AuthJwtPair(jwtUtils.generateAccessTokenForUser(user).getCompactToken(),
                jwtUtils.generateRefreshTokenForUser(user).getCompactToken());
    }

    public AuthJwtPair updateAuthTokens(String refreshToken) {
        if (!jwtUtils.isRefreshTokenValid(refreshToken))
            throw new InvalidRefreshTokenException("Given refresh token is incorrect or outdated");
        Claims jwtBody = jwtUtils.getClaimsFromRefreshTokenString(refreshToken).getPayload();
        String userEmail = jwtBody.getSubject();
        User user = usersRepository.findUserByEmail(userEmail);
        return new AuthJwtPair(jwtUtils.generateAccessTokenForUser(user).getCompactToken(),
                jwtUtils.generateRefreshTokenForUser(user).getCompactToken());
    }
}
