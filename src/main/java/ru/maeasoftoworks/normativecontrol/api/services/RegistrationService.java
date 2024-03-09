package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.AccessToken;
import ru.maeasoftoworks.normativecontrol.api.entities.RefreshToken;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserAlreadyExistsException;
import ru.maeasoftoworks.normativecontrol.api.repositories.AccessTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.RefreshTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.utils.HashingUtils;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.util.List;

@Service
public class RegistrationService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccessTokensRepository accessTokensRepository;

    @Autowired
    private RefreshTokensRepository refreshTokensRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public JwtToken[] registrateUserByCreds(String email, String plainTextPassword) throws UserAlreadyExistsException {
        if (usersRepository.existsByEmail(email))
            throw new UserAlreadyExistsException();

        User user = new User(email, "", "", plainTextPassword, List.of(Role.STUDENT), "UrFU");

        JwtToken jwtAccessToken = jwtUtils.generateAccessTokenForUser(user);
        JwtToken jwtRefreshToken = jwtUtils.generateRefreshTokenForUser(user);

        AccessToken accessToken = new AccessToken();
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        accessToken.setToken(jwtAccessToken.getCompactToken());
        accessToken.setCreatedAt(jwtAccessToken.getJws().getPayload().getIssuedAt());
        accessToken.setExpiresAt(jwtAccessToken.getJws().getPayload().getExpiration());

        accessToken.setUser(user);
        refreshToken.setToken(jwtRefreshToken.getCompactToken());
        refreshToken.setCreatedAt(jwtRefreshToken.getJws().getPayload().getIssuedAt());
        refreshToken.setExpiresAt(jwtRefreshToken.getJws().getPayload().getExpiration());

        usersRepository.save(user);
        accessTokensRepository.save(accessToken);
        refreshTokensRepository.save(refreshToken);


        return new JwtToken[]{jwtAccessToken, jwtRefreshToken};
    }
}
