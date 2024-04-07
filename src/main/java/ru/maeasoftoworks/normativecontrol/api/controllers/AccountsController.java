package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDto;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.services.AccountsService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {
    private final JwtService jwtService;
    private final AccountsService accountsService;

    // TODO: добавить пагинацию, чтобы не выгружать всю базу пользователей в память
    // TODO: рассмотреть вариант возвращать DTO вместо объекта доменной области

    // Администратор может просмотреть аккаунты всех студентов и нормоконтролеров в своём университете
    @GetMapping
    public List<User> getAllUsersAsAdmin(@RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        Admin admin = (Admin) accessJwt.getUser();
        return accountsService.getUsersForAdmin(admin);
    }

    // Администратор может получить данные о любом аккаунте из университета, к которому приписан
    // Другие пользователи - только данные о своём аккаунте
    @GetMapping("/{user_id}")
    public User getUser(@RequestHeader("Authorization") String bearerToken, @PathVariable("user_id") Long userId) {
        String accessToken = bearerToken.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = accessJwt.getUser();
        return accountsService.getOwnUserOrAnyAsAdminById(user, userId);
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
        User userToUpdate = accountsService.getOwnUserOrAnyAsAdminById(user, userId);
        return accountsService.updateUser(userToUpdate, updateUserDto);
    }

    @DeleteMapping(value = "/{user_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject deleteUser(@RequestHeader("Authorization") String bearerToken,
                                 @PathVariable("user_id") Long userId) {
        String accessToken = bearerToken.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = accessJwt.getUser();
        User userToDelete = accountsService.getOwnUserOrAnyAsAdminById(user, userId);
        accountsService.deleteUser(userToDelete);
        JSONObject response = new JSONObject();
        response.put("status", HttpStatus.OK);
        response.put("message", "User with id " + userId + " deleted successfully");
        return response;
    }
}
