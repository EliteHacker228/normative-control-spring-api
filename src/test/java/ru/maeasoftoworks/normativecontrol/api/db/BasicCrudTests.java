package ru.maeasoftoworks.normativecontrol.api.db;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.repositories.*;

import java.util.Collection;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
public class BasicCrudTests {

    @Autowired
    private UniversitiesRepository universitiesRepository;
    @Autowired
    private AdminsRepository adminsRepository;
    @Autowired
    private AcademicGroupsRepository academicGroupsRepository;
    @Autowired
    private NormocontrollersRepository normocontrollersRepository;
    @Autowired
    private StudentsRepository studentsRepository;
    @Autowired
    private UsersRepository usersRepository;

    @Test
    @Transactional
    public void creationOfInstancesForEveryDomainEntityTest(){
        if(!universitiesRepository.existsUniversityByName("УрФУ им. первого президента России Б. Н. Ельцина")) {
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

            admin = Admin.builder()
                    .email("M.D.Sakharov@urfu.me")
                    .password("admin_password")
                    .firstName("Михаил")
                    .middleName("Дмитриевич")
                    .lastName("Сахаров")
                    .isVerified(true)
                    .university(UrFU)
                    .build();
            adminsRepository.save(admin);

            AcademicGroup RI_400015 = new AcademicGroup(UrFU, "РИ-400015");
            academicGroupsRepository.save(RI_400015);

            AcademicGroup RI_400016 = new AcademicGroup(UrFU, "РИ-400016");
            academicGroupsRepository.save(RI_400016);

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

            normocontroller = Normocontroller.builder()
                    .email("D.T.Okhotskiy@urfu.me")
                    .password("normocontroller_password")
                    .firstName("Дмитрий")
                    .middleName("Тимофеевич")
                    .lastName("Охотский")
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
                    .university(UrFU)
                    .academicGroup(RI_400015)
                    .normocontroller(normocontroller)
                    .documentsLimit(5)
                    .build();
            studentsRepository.save(student);

            student = Student.builder()
                    .email("M.S.Jeglov@urfu.me")
                    .password("student_password")
                    .firstName("Михаил")
                    .middleName("Сергеевич")
                    .lastName("Жеглов")
                    .isVerified(true)
                    .university(UrFU)
                    .academicGroup(RI_400015)
                    .normocontroller(normocontroller)
                    .documentsLimit(5)
                    .build();
            usersRepository.save(student);

            student = Student.builder()
                    .email("K.L.Zaycev@urfu.me")
                    .password("student_password")
                    .firstName("Константин")
                    .middleName("Леонидович")
                    .lastName("Зайцев")
                    .isVerified(true)
                    .academicGroup(RI_400016)
                    .normocontroller(normocontroller)
                    .documentsLimit(5)
                    .university(UrFU)
                    .build();
            usersRepository.save(student);
        }

        List<University> universities = universitiesRepository.findAll();
        Assertions.assertTrue(universities.toArray().length >= 1);
        logCollection(universities);

        List<Admin> admins = adminsRepository.findAll();
        Assertions.assertTrue(admins.toArray().length >= 1);
        logCollection(admins);

        List<AcademicGroup> academicGroups = academicGroupsRepository.findAll();
        Assertions.assertTrue(academicGroups.toArray().length >= 1);
        logCollection(academicGroups);

        List<Normocontroller> normocontrollers = normocontrollersRepository.findAll();
        Assertions.assertTrue(normocontrollers.toArray().length >= 1);
        logCollection(normocontrollers);

        List<Student> students = studentsRepository.findAll();
        Assertions.assertTrue(students.toArray().length >= 1);
        logCollection(students);

        List<User> users = usersRepository.findAll();
        Assertions.assertTrue(users.toArray().length >= 1);
        logCollection(users);
    }

    private void logCollection(Collection collection){
        for(Object obj : collection){
            log.info(obj.toString());
        }
    }
}
