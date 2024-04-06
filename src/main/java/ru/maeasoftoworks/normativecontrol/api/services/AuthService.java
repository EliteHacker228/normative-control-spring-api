package ru.maeasoftoworks.normativecontrol.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.login.LoginData;
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
            throw new UserDoesNotExistsException();
        if (!user.getPassword().equals(loginData.getPassword()))
            throw new WrongPasswordException();

        return new AuthJwtPair(jwtUtils.generateAccessTokenForUser(user).getCompactToken(),
                jwtUtils.generateRefreshTokenForUser(user).getCompactToken());
    }

    public AuthJwtPair register(User user) {
        if (usersRepository.existsUserByEmail(user.getEmail()))
            throw new UserAlreadyExistsException();
        usersRepository.save(user);
        return new AuthJwtPair(jwtUtils.generateAccessTokenForUser(user).getCompactToken(),
                jwtUtils.generateRefreshTokenForUser(user).getCompactToken());
    }
}
