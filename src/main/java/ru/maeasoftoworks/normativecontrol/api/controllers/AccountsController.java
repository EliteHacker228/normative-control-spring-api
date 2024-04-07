package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UnauthorizedException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserDoesNotExistsException;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.services.AccountsService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final AccountsService accountsService;

    // TODO: добавить пагинацию, чтобы не выгружать всю базу пользователей в память
    // TODO: рассмотреть вариант возвращать DTO вместо объекта доменной области

    // Администратор может просмотреть аккаунты всех студентов и нормоконтролеров в своём университете
    @GetMapping
    public List<User> getAllUsersAsAdmin(@RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = accessJwt.getUser();
        University university = user.getUniversity();
        List<User> foundUsers = usersRepository.findUsersByUniversity(university);
        return foundUsers.stream().filter(usr -> usr.getRole() != Role.ADMIN || usr == user).toList();
    }

    // Администратор может получить данные о любом аккаунте из университета, к которому приписан
    // Другие пользователи - только данные о своём аккаунте
    @GetMapping("/{user_id}")
    public User getUser(@RequestHeader("Authorization") String bearerToken, @PathVariable("user_id") Long userId) {
        String accessToken = bearerToken.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = accessJwt.getUser();
        University university = user.getUniversity();

        User foundUser = usersRepository.findUsersById(userId);

        if (foundUser == null)
            throw new UserDoesNotExistsException("User with id " + userId + " does not exists");
        if (foundUser.getUniversity() != university)
            throw new UnauthorizedException("You are not authorized to access this resource");
        if (user.getRole() != Role.ADMIN && user != foundUser)
            throw new UnauthorizedException("You are not authorized to access this resource");
        if(user.getRole() == Role.ADMIN && foundUser.getRole() == Role.ADMIN && user != foundUser)
            throw new UnauthorizedException("You are not authorized to access this resource");

        return foundUser;
    }

    // Администратор может обновить данные любого аккаунта в своём университете, кроме аккаунта другого администратора
    // Другие пользователи - только данные своего аккаунта
    @PatchMapping("/{user_id}")
    public User updateUser(@RequestHeader("Authorization") String bearerToken,
                          @PathVariable("user_id") Long userId,
                          @RequestBody UpdateUserDto updateUserDto) {
        String accessToken = bearerToken.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = accessJwt.getUser();
        University university = user.getUniversity();

        User foundUser = usersRepository.findUsersById(userId);

        if (foundUser == null)
            throw new UserDoesNotExistsException("User with id " + userId + " does not exists");
        if (foundUser.getUniversity() != university)
            throw new UnauthorizedException("You are not authorized to access this resource");
        if (user.getRole() != Role.ADMIN && user != foundUser)
            throw new UnauthorizedException("You are not authorized to access this resource");
        if(user.getRole() == Role.ADMIN && foundUser.getRole() == Role.ADMIN && user != foundUser)
            throw new UnauthorizedException("You are not authorized to access this resource");

        return accountsService.updateUser(foundUser, updateUserDto);
    }
}
