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
import ru.maeasoftoworks.normativecontrol.api.utils.hashing.Sha256;

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
        if (!adminsRepository.existsById(1L)) {
            Admin admin = Admin.builder()
                    .email("P.O.Kurchatov@urfu.me")
                    .password(Sha256.getStringSha256("admin_password"))
                    .fullName("Курчатов Павел Олегович")
                    .isVerified(true)
                    .build();
            adminsRepository.save(admin);

            admin = Admin.builder()
                    .email("A.N.Mitkin@urfu.me")
                    .password(Sha256.getStringSha256("admin_password"))
                    .fullName("Митькин Алексей Николаевич")
                    .isVerified(true)
                    .build();
            adminsRepository.save(admin);

            Normocontroller markov = Normocontroller.builder()
                    .email("N.M.Markov@urfu.me")
                    .password(Sha256.getStringSha256("normocontroller_password"))
                    .fullName("Марков Николай Мирославович")
                    .isVerified(true)
                    .build();
            normocontrollersRepository.save(markov);

            Normocontroller levchenko = Normocontroller.builder()
                    .email("A.V.Levchenko@urfu.me")
                    .password(Sha256.getStringSha256("normocontroller_password"))
                    .fullName("Левченко Антон Валерьевич")
                    .isVerified(true)
                    .build();
            normocontrollersRepository.save(levchenko);

            AcademicGroup RI_400015 = new AcademicGroup("РИ-400015", levchenko);
            academicGroupsRepository.save(RI_400015);

            AcademicGroup RI_400016 = new AcademicGroup("РИ-400016", markov);
            academicGroupsRepository.save(RI_400016);

            Student student = Student.builder()
                    .email("I.A.Sharapov@urfu.me")
                    .password(Sha256.getStringSha256("student_password"))
                    .fullName("Шарапов Игорь Анатольевич")
                    .isVerified(true)
                    .academicGroup(RI_400015)
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
}
