package ru.maeasoftoworks.normativecontrol.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserDoesNotExistsException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.WrongPasswordException;
import ru.maeasoftoworks.normativecontrol.api.repositories.AdminsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.NormocontrollersRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.StudentsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.JwtUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final JwtUtils jwtUtils;

    public Jwt[] loginByCredentials(String email, String password){
        User user = usersRepository.findUserByEmail(email);
        if(user == null)
            throw new UserDoesNotExistsException();
        if(!user.getPassword().equals(password))
            throw new WrongPasswordException();
        return new Jwt[]{jwtUtils.generateAccessTokenForUser(user), jwtUtils.generateRefreshTokenForUser(user)};
    }
}
