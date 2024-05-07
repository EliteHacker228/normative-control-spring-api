package ru.maeasoftoworks.normativecontrol.api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDocumentsLimitDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserEmailDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserPasswordDto;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.services.AccountsService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;

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
    public List<User> getAllUsers() {
        return accountsService.getUsers();
    }

    // Администратор может получить данные о любом аккаунте из университета, к которому приписан
    // Другие пользователи - только данные о своём аккаунте
    @GetMapping("/{user_id}")
    public User getUserById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("user_id") Long userId) {
        return accountsService.getUserById(userId);
    }

    // Администратор может обновить данные любого аккаунта в своём университете, кроме аккаунта другого администратора
    // Другие пользователи - только данные своего аккаунта
    @PatchMapping("/{user_id}")
    public User updateUserById(@PathVariable("user_id") Long userId,
                               @RequestBody UpdateUserDto updateUserDto) {
        return accountsService.updateUserById(userId, updateUserDto);
    }

    @DeleteMapping("/{user_id}")
    public JSONObject deleteUser(@PathVariable("user_id") Long userId) {
        accountsService.deleteUserById(userId);
        JSONObject response = new JSONObject();
        response.put("message", "User with id " + userId + " deleted successfully");
        return response;
    }

    @PatchMapping("/{user_id}/email")
    public AuthJwtPair updateUserEmail(@PathVariable("user_id") Long userId,
                                       @RequestBody @Valid UpdateUserEmailDto updateUserEmailDto) {
        return accountsService.updateUserEmailById(userId, updateUserEmailDto);
    }

    @PatchMapping("/{user_id}/password")
    public User updateUserEmail(@PathVariable("user_id") Long userId,
                                @RequestBody @Valid UpdateUserPasswordDto updateUserPasswordDto) {
        return accountsService.updateUserPasswordById(userId, updateUserPasswordDto);
    }

    @PatchMapping("/{user_id}/documents-limit")
    public User updateUserEmail(@PathVariable("user_id") Long userId,
                                @RequestBody UpdateUserDocumentsLimitDto updateUserDocumentsLimitDto) {
        return accountsService.updateUserDocumentsLimitById(userId, updateUserDocumentsLimitDto);
    }

    // TODO: Реализовать функционал подтверждения действий
    @DeleteMapping("/{user_id}/verifications/{verification_link_id}")
    public ResponseEntity<JSONObject> verifyUserAction(@PathVariable("user_id") Long userId,
                                                       @PathVariable("verification_link_id") String verificationLinkId) {
        JSONObject response = new JSONObject();
        response.put("message", "Verification functional is not implemented yet");
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }
}
