package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.entities.AccessToken;

public interface AccessTokensRepository extends JpaRepository<AccessToken, Long> {
    AccessToken findAccessTokensByUserId(long userId);

    boolean existsAccessTokensByUserId(long userId);
}
