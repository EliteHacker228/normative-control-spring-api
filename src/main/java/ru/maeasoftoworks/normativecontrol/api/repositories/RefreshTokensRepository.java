package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.auth.RefreshToken;

public interface RefreshTokensRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findRefreshTokenByUserId(Long userId);
    RefreshToken findRefreshTokenByToken(String token);
}
