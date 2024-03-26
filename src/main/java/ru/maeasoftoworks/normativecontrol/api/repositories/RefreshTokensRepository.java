package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.entities.RefreshToken;
import ru.maeasoftoworks.normativecontrol.api.entities.User;

public interface RefreshTokensRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findRefreshTokensByUserId(long userId);

    boolean existsRefreshTokensByUserId(long userId);

    boolean existsRefreshTokenByToken(String token);

    RefreshToken findRefreshTokenByToken(String token);

    void deleteByUser(User user);
}
