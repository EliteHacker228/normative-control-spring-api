package ru.maeasoftoworks.normativecontrol.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;

public interface StudentsRepository extends JpaRepository<Student, Long> {
    Student findStudentById(Long id);
    Student findStudentByEmail(String email);
}
