package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.University;

public interface UniversitiesRepository extends JpaRepository<University, Long> {
    University findUniversityById(Long id);
    boolean existsUniversityByName(String name);
}
