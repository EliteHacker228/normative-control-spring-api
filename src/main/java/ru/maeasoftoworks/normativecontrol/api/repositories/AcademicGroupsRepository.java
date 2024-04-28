package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;

import java.util.List;

public interface AcademicGroupsRepository extends JpaRepository<AcademicGroup, Long> {
    AcademicGroup findAcademicGroupById(Long id);
    List<AcademicGroup> findAcademicGroupsByNormocontrollerId(Long normocontrolerId);
}
