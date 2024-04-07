package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDocumentsLimitDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserEmailDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserPasswordDto;
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
    public List<User> getAllUsersAsAdmin(@RequestHeader("Authorization") String authorizationHeader) {
        Admin admin = (Admin) getUserFromAuthorizationHeader(authorizationHeader);
        return accountsService.getUsersForAdmin(admin);
    }

    // Администратор может получить данные о любом аккаунте из университета, к которому приписан
    // Другие пользователи - только данные о своём аккаунте
    @GetMapping("/{user_id}")
    public User getUser(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("user_id") Long userId) {
        User user = getUserFromAuthorizationHeader(authorizationHeader);
        return accountsService.getOwnUserOrAnyAsAdminById(user, userId);
    }

    // Администратор может обновить данные любого аккаунта в своём университете, кроме аккаунта другого администратора
    // Другие пользователи - только данные своего аккаунта
    @PatchMapping("/{user_id}")
    public User updateUser(@RequestHeader("Authorization") String authorizationHeader,
                           @PathVariable("user_id") Long userId,
                           @RequestBody UpdateUserDto updateUserDto) {
        User user = getUserFromAuthorizationHeader(authorizationHeader);
        User userToUpdate = accountsService.getOwnUserOrAnyAsAdminById(user, userId);
        return accountsService.updateUser(userToUpdate, updateUserDto);
    }

    @DeleteMapping("/{user_id}")
    public JSONObject deleteUser(@RequestHeader("Authorization") String authorizationHeader,
                                 @PathVariable("user_id") Long userId) {
        User user = getUserFromAuthorizationHeader(authorizationHeader);
        User userToDelete = accountsService.getOwnUserOrAnyAsAdminById(user, userId);
        accountsService.deleteUser(userToDelete);
        JSONObject response = new JSONObject();
        response.put("status", HttpStatus.OK);
        response.put("message", "User with id " + userId + " deleted successfully");
        return response;
    }

    @PatchMapping("/{user_id}/email")
    public User updateUserEmail(@RequestHeader("Authorization") String authorizationHeader,
                                @PathVariable("user_id") Long userId,
                                @RequestBody UpdateUserEmailDto updateUserEmailDto) {
        User user = getUserFromAuthorizationHeader(authorizationHeader);
        User userToUpdateEmail = accountsService.getOwnUserOrAnyAsAdminById(user, userId);
        return accountsService.updateUserEmail(userToUpdateEmail, updateUserEmailDto);
    }

    @PatchMapping("/{user_id}/password")
    public User updateUserEmail(@RequestHeader("Authorization") String authorizationHeader,
                                @PathVariable("user_id") Long userId,
                                @RequestBody UpdateUserPasswordDto updateUserPasswordDto) {
        User user = getUserFromAuthorizationHeader(authorizationHeader);
        User userToUpdateEmail = accountsService.getOwnUserOrAnyAsAdminById(user, userId);
        return accountsService.updateUserPassword(userToUpdateEmail, updateUserPasswordDto);
    }

    @PatchMapping("/{user_id}/documents-limit")
    public User updateUserEmail(@RequestHeader("Authorization") String authorizationHeader,
                                @PathVariable("user_id") Long userId,
                                @RequestBody UpdateUserDocumentsLimitDto updateUserDocumentsLimitDto) {
        User user = getUserFromAuthorizationHeader(authorizationHeader);
        User userToUpdateEmail = accountsService.getOwnUserOrAnyAsAdminById(user, userId);
        return accountsService.updateUserDocumentsLimit(userToUpdateEmail, updateUserDocumentsLimitDto);
    }

    @DeleteMapping("/{user_id}/verifications/{verification_link_id}")
    public ResponseEntity<JSONObject> verifyUserAction(@RequestHeader("Authorization") String authorizationHeader,
                                                       @PathVariable("user_id") Long userId,
                                                       @PathVariable("verification_link_id") Long verificationLinkId) {
        JSONObject response = new JSONObject();
        response.put("status", HttpStatus.NOT_IMPLEMENTED.value());
        response.put("message", "Verification functional is not implemented yet");

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }

    private User getUserFromAuthorizationHeader(String authHeader) {
        String accessToken = authHeader.substring(("Bearer ").length());
        Jwt accessJwt = jwtService.getJwtFromAccessTokenString(accessToken);
        return accessJwt.getUser();
    }
}
