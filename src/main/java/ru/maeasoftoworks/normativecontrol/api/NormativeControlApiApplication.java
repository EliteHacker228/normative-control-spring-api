package ru.maeasoftoworks.normativecontrol.api;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.repositories.*;

//@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class NormativeControlApiApplication {
    @Autowired
    private AdminsRepository adminsRepository;
    @Autowired
    private AcademicGroupsRepository academicGroupsRepository;
    @Autowired
    private NormocontrollersRepository normocontrollersRepository;
    @Autowired
    private StudentsRepository studentsRepository;
    @Autowired
    private DocumentsRepository documentsRepository;
    @Autowired
    private ResultsRepository resultsRepository;

    public static void main(String[] args) {
        SpringApplication.run(NormativeControlApiApplication.class, args);
    }

    @PostConstruct
    @Transactional
    protected void initDatabase() {
        Admin admin = Admin.builder()
                .email("P.O.Kurchatov@urfu.me")
                .password("admin_password")
                .firstName("Павел")
                .middleName("Олегович")
                .lastName("Курчатов")
                .isVerified(true)
                .build();
        adminsRepository.save(admin);

        admin = Admin.builder()
                .email("A.N.Mitkin@urfu.me")
                .password("admin_password")
                .firstName("Алексей")
                .middleName("Николаевич")
                .lastName("Митькин")
                .isVerified(true)
                .build();
        adminsRepository.save(admin);

        AcademicGroup RI_400015 = new AcademicGroup("РИ-400015");
        academicGroupsRepository.save(RI_400015);

        AcademicGroup RI_400016 = new AcademicGroup("РИ-400016");
        academicGroupsRepository.save(RI_400016);

        Normocontroller normocontroller = Normocontroller.builder()
                .email("N.M.Markov@urfu.me")
                .password("normocontroller_password")
                .firstName("Николай")
                .middleName("Мирославович")
                .lastName("Марков")
                .isVerified(true)
                .build();
        normocontrollersRepository.save(normocontroller);

        normocontroller = Normocontroller.builder()
                .email("A.V.Levchenko@urfu.me")
                .password("normocontroller_password")
                .firstName("Антон")
                .middleName("Валерьевич")
                .lastName("Левченко")
                .isVerified(true)
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

//        Document document = Document.builder()
//                .user(student)
//                .studentName("Шарапов И.А.")
//                .academicGroup(RI_400015)
//                .fileName("И.А.Шарапов РИ-400015 ВКР")
//                .isReported(false)
//                .comment("")
//                .build();
//        documentsRepository.save(document);

//        Result result = new Result(document, VerificationStatus.PENDING);
//        resultsRepository.save(result);
    }
}
