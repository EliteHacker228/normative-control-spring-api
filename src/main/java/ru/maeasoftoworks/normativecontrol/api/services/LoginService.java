package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.entities.AccessToken;
import ru.maeasoftoworks.normativecontrol.api.entities.RefreshToken;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.repositories.AccessTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.RefreshTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.utils.HashingUtils;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

@Service
public class LoginService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccessTokensRepository accessTokensRepository;

    @Autowired
    private RefreshTokensRepository refreshTokensRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @SneakyThrows
    @Transactional
    public JwtToken[] loginUser(String email, String plainTextPassword) {
        String userHashedPassword = HashingUtils.sha256(plainTextPassword);
        User foundUser = usersRepository.findByEmail(email);

        if (foundUser == null || !foundUser.getPassword().equals(userHashedPassword))
            return new JwtToken[]{};

        JwtToken jwtAccessToken = jwtUtils.generateAccessTokenForUser(foundUser);
        JwtToken jwtRefreshToken = jwtUtils.generateRefreshTokenForUser(foundUser);

        AccessToken accessToken = new AccessToken();
        RefreshToken refreshToken = new RefreshToken();

        if (accessTokensRepository.existsAccessTokensByUserId(foundUser.getId())) {
            accessToken = accessTokensRepository.findAccessTokensByUserId(foundUser.getId());
            refreshToken = refreshTokensRepository.findRefreshTokensByUserId(foundUser.getId());
        }

        refreshToken.setUser(foundUser);
        accessToken.setToken(jwtAccessToken.getCompactToken());
        accessToken.setCreatedAt(jwtAccessToken.getJws().getPayload().getIssuedAt());
        accessToken.setExpiresAt(jwtAccessToken.getJws().getPayload().getExpiration());

        accessToken.setUser(foundUser);
        refreshToken.setToken(jwtRefreshToken.getCompactToken());
        refreshToken.setCreatedAt(jwtRefreshToken.getJws().getPayload().getIssuedAt());
        refreshToken.setExpiresAt(jwtRefreshToken.getJws().getPayload().getExpiration());

        accessTokensRepository.save(accessToken);
        refreshTokensRepository.save(refreshToken);


        return new JwtToken[]{jwtAccessToken, jwtRefreshToken};
    }
}
