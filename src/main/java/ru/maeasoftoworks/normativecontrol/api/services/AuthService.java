package ru.maeasoftoworks.normativecontrol.api.services;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.auth.RefreshToken;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.LoginData;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.RegisterNormocontrollerDto;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.RegisterStudentDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.*;
import ru.maeasoftoworks.normativecontrol.api.repositories.*;
import ru.maeasoftoworks.normativecontrol.api.utils.hashing.Sha256;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

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
        if (!user.getPassword().equals(Sha256.getStringSha256(loginData.getPassword())))
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

    @Transactional
    public AuthJwtPair registerStudent(RegisterStudentDto registerStudentDto) {
        if (usersRepository.existsUserByEmail(registerStudentDto.getEmail()))
            throw new ResourceAlreadyExistsException("User with e-mail " + registerStudentDto.getEmail() + " already exists");

        AcademicGroup academicGroup = null;
        if(registerStudentDto.getAcademicGroupId() != null)
            academicGroup = academicGroupsRepository.findAcademicGroupById(registerStudentDto.getAcademicGroupId());

        Student student = Student.builder()
                .email(registerStudentDto.getEmail())
                .fullName(registerStudentDto.getFullName())
                .academicGroup(academicGroup)
                .password(Sha256.getStringSha256(registerStudentDto.getPassword()))
                .isVerified(false)
                .documentsLimit(5)
                .build();

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

    @Transactional
    public AuthJwtPair registerNormocontroller(RegisterNormocontrollerDto registerNormocontrollerDto) {
        if (usersRepository.existsUserByEmail(registerNormocontrollerDto.getEmail()))
            throw new ResourceAlreadyExistsException("User with e-mail " + registerNormocontrollerDto.getEmail() + " already exists");

        Normocontroller normocontroller = Normocontroller.builder()
                .email(registerNormocontrollerDto.getEmail())
                .password(Sha256.getStringSha256(registerNormocontrollerDto.getPassword()))
                .fullName(registerNormocontrollerDto.getFullName())
                .isVerified(false)
                .build();
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

    @Transactional
    public AuthJwtPair updateAuthTokens(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken))
            throw new InvalidRefreshTokenException("Given refresh token is incorrect or outdated");
        Claims jwtBody = jwtService.getClaimsFromRefreshTokenString(refreshToken).getPayload();
        String userEmail = jwtBody.getSubject();
        User user = usersRepository.findUserByEmail(userEmail);

        RefreshToken previousRefreshToken = refreshTokensRepository.findRefreshTokenByUserId(user.getId());
        if (previousRefreshToken == null || !previousRefreshToken.getToken().equals(refreshToken)) {
            throw new UnauthorizedException("Refresh token invalid. Try to login again");
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
