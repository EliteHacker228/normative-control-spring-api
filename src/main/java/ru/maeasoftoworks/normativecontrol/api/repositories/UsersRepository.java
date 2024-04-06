package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;

public interface UsersRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    boolean existsUserByEmail(String email);
}
