package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.RefreshToken;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.exceptions.AccessTokenRefreshFailedException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserAlreadyExistsException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.WrongCredentialsException;
import ru.maeasoftoworks.normativecontrol.api.repositories.RefreshTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.utils.HashingUtils;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountService {
    private UsersRepository usersRepository;
    private RefreshTokensRepository refreshTokensRepository;
    private JwtUtils jwtUtils;

    @Transactional
    public JwtToken[] loginUserByCreds(String email, String plainTextPassword) {
        String userHashedPassword = HashingUtils.sha256(plainTextPassword);
        User foundUser = usersRepository.findByEmail(email);

        if (foundUser == null || !foundUser.getPassword().equals(userHashedPassword))
            throw new WrongCredentialsException();

        JwtToken jwtAccessToken = jwtUtils.generateAccessTokenForUser(foundUser);
        JwtToken jwtRefreshToken = jwtUtils.generateRefreshTokenForUser(foundUser);

        RefreshToken refreshToken = new RefreshToken();

        if (refreshTokensRepository.existsRefreshTokensByUserId(foundUser.getId())) {
            refreshToken = refreshTokensRepository.findRefreshTokensByUserId(foundUser.getId());
        }

        refreshToken.setUser(foundUser);
        refreshToken.setToken(jwtRefreshToken.getCompactToken());
        refreshToken.setCreatedAt(jwtRefreshToken.getJws().getPayload().getIssuedAt());
        refreshToken.setExpiresAt(jwtRefreshToken.getJws().getPayload().getExpiration());

        refreshTokensRepository.save(refreshToken);


        return new JwtToken[]{jwtAccessToken, jwtRefreshToken};
    }

    @Transactional
    public JwtToken[] registrateUserByCreds(String email, String plainTextPassword) {

        if (usersRepository.existsByEmail(email))
            throw new UserAlreadyExistsException();

        User user = new User(email, "", "", plainTextPassword, List.of(Role.STUDENT), "UrFU");

        JwtToken jwtAccessToken = jwtUtils.generateAccessTokenForUser(user);
        JwtToken jwtRefreshToken = jwtUtils.generateRefreshTokenForUser(user);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(jwtRefreshToken.getCompactToken());
        refreshToken.setCreatedAt(jwtRefreshToken.getJws().getPayload().getIssuedAt());
        refreshToken.setExpiresAt(jwtRefreshToken.getJws().getPayload().getExpiration());

        usersRepository.save(user);
        refreshTokensRepository.save(refreshToken);


        return new JwtToken[]{jwtAccessToken, jwtRefreshToken};
    }

    @Transactional
    public JwtToken[] updateAccessTokenByRefreshToken(String compactRefreshToken) {
        if (jwtUtils.isRefreshTokenReadable(compactRefreshToken)) {
            if (!jwtUtils.isRefreshTokenExpired(compactRefreshToken)) {
                if (refreshTokensRepository.existsRefreshTokenByToken(compactRefreshToken)) {
                    //Генерируем новый аксес токен и возвращаем его
                    //а также новый рефреш, сохраняем его в бд, тем самым инвалидировав старый,
                    //а потом возвращаем оба токена пользователю
                    RefreshToken currentRefreshToken = refreshTokensRepository.findRefreshTokenByToken(compactRefreshToken);
                    User user = currentRefreshToken.getUser();

                    JwtToken newRefreshToken = jwtUtils.generateRefreshTokenForUser(user);
                    JwtToken newAccessToken = jwtUtils.generateAccessTokenForUser(user);

                    currentRefreshToken.setToken(newRefreshToken.getCompactToken());
                    currentRefreshToken.setCreatedAt(newRefreshToken.getJws().getPayload().getIssuedAt());
                    currentRefreshToken.setExpiresAt(newRefreshToken.getJws().getPayload().getExpiration());
                    refreshTokensRepository.save(currentRefreshToken);

                    return new JwtToken[]{newAccessToken, newRefreshToken};
                } else {
                    throw new AccessTokenRefreshFailedException("Refresh token does not exists");
                }
            } else {
                if (refreshTokensRepository.existsRefreshTokenByToken(compactRefreshToken)) {
                    // Аксес и рефреш токены не валидны, пользователь должен будет войти заново, возвращаем 403
                    throw new AccessTokenRefreshFailedException("Refresh token expired. Please, log-in again");
                } else {
                    // Возвращаем 400
                    throw new AccessTokenRefreshFailedException("Refresh token does not exists");
                }
            }
        } else {
            // Возвращаем 400
            throw new AccessTokenRefreshFailedException("Refresh is incorrect");
        }
    }
}
