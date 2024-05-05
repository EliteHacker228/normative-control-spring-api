package ru.maeasoftoworks.normativecontrol.api.services;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.auth.RefreshToken;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.LoginData;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.RegisterDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.*;
import ru.maeasoftoworks.normativecontrol.api.repositories.*;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsersRepository usersRepository;
    private final StudentsRepository studentsRepository;
    private final NormocontrollersRepository normocontrollersRepository;
    private final AcademicGroupsRepository academicGroupsRepository;
    private final RefreshTokensRepository refreshTokensRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthJwtPair login(LoginData loginData) {
        User user = usersRepository.findUserByEmail(loginData.getEmail());
        if (user == null)
            throw new UserDoesNotExistsException("User with e-mail " + loginData.getEmail() + " does not exists");
        if (!user.getPassword().equals(loginData.getPassword()))
            throw new WrongPasswordException("Given password is incorrect");

        RefreshToken previousRefreshToken = refreshTokensRepository.findRefreshTokenByUserId(user.getId());
        if (previousRefreshToken == null) {
            Jwt accessTokenJwt = jwtService.generateAccessTokenForUser(user);
            Jwt refreshTokenJwt = jwtService.generateRefreshTokenForUser(user);

            RefreshToken newRefreshToken = RefreshToken.builder()
                    .user(refreshTokenJwt.getUser())
                    .token(refreshTokenJwt.getCompactToken())
                    .createdAt(refreshTokenJwt.getJws().getPayload().getIssuedAt())
                    .expiresAt(refreshTokenJwt.getJws().getPayload().getExpiration())
                    .build();
            refreshTokensRepository.save(newRefreshToken);

            return new AuthJwtPair(accessTokenJwt.getCompactToken(),
                    refreshTokenJwt.getCompactToken());
        }

        Jwt accessTokenJwt = jwtService.generateAccessTokenForUser(user);
        Jwt refreshTokenJwt = jwtService.generateRefreshTokenForUser(user);

        previousRefreshToken.setToken(refreshTokenJwt.getCompactToken());
        previousRefreshToken.setCreatedAt(refreshTokenJwt.getJws().getPayload().getIssuedAt());
        previousRefreshToken.setExpiresAt(refreshTokenJwt.getJws().getPayload().getExpiration());
        refreshTokensRepository.save(previousRefreshToken);

        return new AuthJwtPair(accessTokenJwt.getCompactToken(),
                refreshTokenJwt.getCompactToken());
    }

    // TODO: Разделить на 2 метода
    @Transactional
    public AuthJwtPair register(RegisterDto registerDto) {
        if (usersRepository.existsUserByEmail(registerDto.getEmail()))
            throw new UserAlreadyExistsException("User with e-mail " + registerDto.getEmail() + " already exists");

        log.info(registerDto.toString());

        if (registerDto.getRole() == Role.STUDENT) {
            AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(registerDto.getAcademicGroupId());

            Student student = Student.builder()
                    .email(registerDto.getEmail())
                    .fullName(registerDto.getFullName())
                    .academicGroup(academicGroup)
                    .password(registerDto.getPassword())
                    .isVerified(false)
                    .documentsLimit(5)
                    .build();

            log.info(student.toString());

            studentsRepository.save(student);

            Jwt accessTokenJwt = jwtService.generateAccessTokenForUser(student);
            Jwt refreshTokenJwt = jwtService.generateRefreshTokenForUser(student);

            RefreshToken newRefreshToken = RefreshToken.builder()
                    .user(refreshTokenJwt.getUser())
                    .token(refreshTokenJwt.getCompactToken())
                    .createdAt(refreshTokenJwt.getJws().getPayload().getIssuedAt())
                    .expiresAt(refreshTokenJwt.getJws().getPayload().getExpiration())
                    .build();
            refreshTokensRepository.save(newRefreshToken);

            return new AuthJwtPair(accessTokenJwt.getCompactToken(),
                    accessTokenJwt.getCompactToken());
        }

        if (registerDto.getRole() == Role.NORMOCONTROLLER) {

            Normocontroller normocontroller = Normocontroller.builder()
                    .email(registerDto.getEmail())
                    .password(registerDto.getPassword())
                    .fullName(registerDto.getFullName())
                    .isVerified(false)
                    .build();
            log.info(normocontroller.toString());
            normocontrollersRepository.save(normocontroller);

            Jwt accessTokenJwt = jwtService.generateAccessTokenForUser(normocontroller);
            Jwt refreshTokenJwt = jwtService.generateRefreshTokenForUser(normocontroller);

            RefreshToken newRefreshToken = RefreshToken.builder()
                    .user(refreshTokenJwt.getUser())
                    .token(refreshTokenJwt.getCompactToken())
                    .createdAt(refreshTokenJwt.getJws().getPayload().getIssuedAt())
                    .expiresAt(refreshTokenJwt.getJws().getPayload().getExpiration())
                    .build();
            refreshTokensRepository.save(newRefreshToken);

            return new AuthJwtPair(accessTokenJwt.getCompactToken(),
                    accessTokenJwt.getCompactToken());
        }

        throw new RuntimeException("Registration failed");
    }

    @Transactional
    public AuthJwtPair updateAuthTokens(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken))
            throw new InvalidRefreshTokenException("Given refresh token is incorrect or outdated");
        Claims jwtBody = jwtService.getClaimsFromRefreshTokenString(refreshToken).getPayload();
        String userEmail = jwtBody.getSubject();
        User user = usersRepository.findUserByEmail(userEmail);

        RefreshToken previousRefreshToken = refreshTokensRepository.findRefreshTokenByUserId(user.getId());
        if (previousRefreshToken == null || !previousRefreshToken.getToken().equals(refreshToken)) {
            throw new UnauthorizedException("Refresh token not allowed. Try to login again");
        }

        Jwt accessTokenJwt = jwtService.generateAccessTokenForUser(user);
        Jwt refreshTokenJwt = jwtService.generateRefreshTokenForUser(user);

        previousRefreshToken.setToken(refreshTokenJwt.getCompactToken());
        previousRefreshToken.setCreatedAt(refreshTokenJwt.getJws().getPayload().getIssuedAt());
        previousRefreshToken.setExpiresAt(refreshTokenJwt.getJws().getPayload().getExpiration());
        refreshTokensRepository.save(previousRefreshToken);

        return new AuthJwtPair(accessTokenJwt.getCompactToken(),
                refreshTokenJwt.getCompactToken());
    }
}
