package ru.maeasoftoworks.normativecontrol.api.services;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.LoginData;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.RegisterDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.*;
import ru.maeasoftoworks.normativecontrol.api.repositories.AcademicGroupsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.NormocontrollersRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.StudentsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsersRepository usersRepository;
    private final StudentsRepository studentsRepository;
    private final NormocontrollersRepository normocontrollersRepository;
    private final AcademicGroupsRepository academicGroupsRepository;
    private final JwtService jwtService;

    public AuthJwtPair login(LoginData loginData) {
        User user = usersRepository.findUserByEmail(loginData.getEmail());
        if (user == null)
            throw new UserDoesNotExistsException("User with e-mail " + loginData.getEmail() + " does not exists");
        if (!user.getPassword().equals(loginData.getPassword()))
            throw new WrongPasswordException("Given password is incorrect");

        return new AuthJwtPair(jwtService.generateAccessTokenForUser(user).getCompactToken(),
                jwtService.generateRefreshTokenForUser(user).getCompactToken());
    }

    // TODO: Разделить на 2 метода
    @Transactional
    public AuthJwtPair register(RegisterDto registerDto) {
        if (usersRepository.existsUserByEmail(registerDto.getEmail()))
            throw new UserAlreadyExistsException("User with e-mail " + registerDto.getEmail() + " already exists");

        log.info(registerDto.toString());

        if (registerDto.getRole() == Role.STUDENT) {
            AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(registerDto.getAcademicGroupId());
            Normocontroller normocontroller = normocontrollersRepository.findNormocontrollerById(registerDto.getNormocontrollerId());

            Student student = Student.builder()
                    .email(registerDto.getEmail())
                    .lastName(registerDto.getLastName())
                    .firstName(registerDto.getFirstName())
                    .middleName(registerDto.getMiddleName())
                    .academicGroup(academicGroup)
                    .normocontroller(normocontroller)
                    .password(registerDto.getPassword())
                    .isVerified(false)
                    .documentsLimit(5)
                    .build();

            log.info(student.toString());

            studentsRepository.save(student);
            return new AuthJwtPair(jwtService.generateAccessTokenForUser(student).getCompactToken(),
                    jwtService.generateRefreshTokenForUser(student).getCompactToken());
        }

        if (registerDto.getRole() == Role.NORMOCONTROLLER) {

            Normocontroller normocontroller = Normocontroller.builder()
                    .email(registerDto.getEmail())
                    .password(registerDto.getPassword())
                    .firstName(registerDto.getFirstName())
                    .middleName(registerDto.getMiddleName())
                    .lastName(registerDto.getLastName())
                    .isVerified(false)
                    .build();
            log.info(normocontroller.toString());

            normocontrollersRepository.save(normocontroller);
            return new AuthJwtPair(jwtService.generateAccessTokenForUser(normocontroller).getCompactToken(),
                    jwtService.generateRefreshTokenForUser(normocontroller).getCompactToken());
        }

        throw new RuntimeException("Registration failed");
    }

    public AuthJwtPair updateAuthTokens(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken))
            throw new InvalidRefreshTokenException("Given refresh token is incorrect or outdated");
        Claims jwtBody = jwtService.getClaimsFromRefreshTokenString(refreshToken).getPayload();
        String userEmail = jwtBody.getSubject();
        User user = usersRepository.findUserByEmail(userEmail);
        return new AuthJwtPair(jwtService.generateAccessTokenForUser(user).getCompactToken(),
                jwtService.generateRefreshTokenForUser(user).getCompactToken());
    }
}
