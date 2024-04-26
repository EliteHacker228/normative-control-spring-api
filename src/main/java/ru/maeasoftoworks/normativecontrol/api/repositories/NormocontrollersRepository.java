package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;

import java.util.List;

public interface NormocontrollersRepository extends JpaRepository<Normocontroller, Long> {
    Normocontroller findNormocontrollerById(Long id);
    Normocontroller findNormocontrollerByEmail(String email);
}
