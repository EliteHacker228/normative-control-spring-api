package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;

import java.util.List;

public interface UsersRepository extends JpaRepository<User, Long> {
    User findUsersById(Long id);
    User findUserByEmail(String email);
    List<User> findUsersByUniversity(University university);
    boolean existsUserByEmail(String email);
}
