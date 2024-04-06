package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UnauthorizedException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserDoesNotExistsException;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {
    private final UsersRepository usersRepository;
    private final JwtService jwtService;

    // TODO: добавить пагинацию, чтобы не выгружать всю базу пользователей в память
    // TODO: рассмотреть вариант возвращать DTO вместо объекта доменной области
    @GetMapping
    public List<User> getAllUsersAsAdmin(@RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = accessJwt.getUser();
        University university = user.getUniversity();
        return usersRepository.findUsersByUniversity(university);
    }

    @GetMapping("/{user_id}")
    public User getUserAsAdmin(@RequestHeader("Authorization") String bearerToken,
                                     @PathVariable("user_id") Long userId) {
        String accessToken = bearerToken.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = accessJwt.getUser();
        University university = user.getUniversity();

        User foundUser = usersRepository.findUsersById(userId);

        if(foundUser == null)
            throw new UserDoesNotExistsException("User with id " + userId + " does not exists");
        if(foundUser.getUniversity() != university)
            throw new UnauthorizedException("You are not authorized to access this resource");
        if(user.getRole() != Role.ADMIN && user != foundUser)
            throw new UnauthorizedException("You are not authorized to access this resource");

        return foundUser;
    }
}
