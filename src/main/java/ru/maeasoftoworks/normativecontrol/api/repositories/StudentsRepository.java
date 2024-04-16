package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;

import java.util.List;

public interface StudentsRepository extends JpaRepository<Student, Long> {
    Student findStudentById(Long id);
    Student findStudentByEmail(String email);
    List<Student> findStudentsByAcademicGroup(AcademicGroup academicGroup);

    List<Student> findStudentsByNormocontrollerId(Long normocontrollerId);
}
