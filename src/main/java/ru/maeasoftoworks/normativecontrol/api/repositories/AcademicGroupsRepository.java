package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.AcademicGroup;

public interface AcademicGroupsRepository extends JpaRepository<AcademicGroup, Long> {
    AcademicGroup findAcademicGroupById(Long id);
}
