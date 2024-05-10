package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;

public interface AdminsRepository extends JpaRepository<Admin, Long> {
    Admin findAdminById(Long id);
    boolean existsAdminByEmail(String email);
    Admin findAdminByEmail(String email);
}
