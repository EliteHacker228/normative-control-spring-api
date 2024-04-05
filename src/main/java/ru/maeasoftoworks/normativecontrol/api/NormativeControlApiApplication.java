package ru.maeasoftoworks.normativecontrol.api;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import ru.maeasoftoworks.normativecontrol.api.domain.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.repositories.*;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@RequiredArgsConstructor
@Slf4j
public class NormativeControlApiApplication {

    @Autowired
    private UniversitiesRepository universitiesRepository;
    @Autowired
    private AdminsRepository adminsRepository;
    @Autowired
    private AcacdemicGroupsRepository acacdemicGroupsRepository;
    @Autowired
    private NormocontrollersRepository normocontrollersRepository;
    @Autowired
    private StudentsRepository studentsRepository;

    public static void main(String[] args) {
        SpringApplication.run(NormativeControlApiApplication.class, args);
    }

    @PostConstruct
    @Transactional
    protected void initDatabase() {
        University UrFU = new University("УрФУ им. первого президента России Б. Н. Ельцина");
        universitiesRepository.save(UrFU);

        Admin admin = Admin.builder()
                .email("P.O.Kurchatov@urfu.me")
                .password("admin_password")
                .firstName("Павел")
                .middleName("Олегович")
                .lastName("Курчатов")
                .isVerified(true)
                .university(UrFU)
                .build();
        adminsRepository.save(admin);

        AcademicGroup RI_400015 = new AcademicGroup(UrFU, "РИ-400015");
        acacdemicGroupsRepository.save(RI_400015);

        Normocontroller normocontroller = Normocontroller.builder()
                .email("A.V.Levchenko@urfu.me")
                .password("normocontroller_password")
                .firstName("Антон")
                .middleName("Валерьевич")
                .lastName("Левченко")
                .isVerified(true)
                .university(UrFU)
                .documentsLimit(30)
                .build();
        normocontrollersRepository.save(normocontroller);

        Student student = Student.builder()
                .email("I.A.Sharapov@urfu.me")
                .password("student_password")
                .firstName("Игорь")
                .middleName("Анатольевич")
                .lastName("Шарапов")
                .isVerified(true)
                .academicGroup(RI_400015)
                .normocontroller(normocontroller)
                .documentsLimit(5)
                .build();
        studentsRepository.save(student);
    }
}
