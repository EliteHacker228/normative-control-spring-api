package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;

public interface NormocontrollersRepository extends JpaRepository<Normocontroller, Long> {
    Normocontroller findNormocontrollerByEmail(String email);
}
