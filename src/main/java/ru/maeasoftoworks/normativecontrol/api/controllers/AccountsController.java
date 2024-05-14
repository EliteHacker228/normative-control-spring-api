package ru.maeasoftoworks.normativecontrol.api.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
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
@Tag(name = "Accounts", description = "Отвечает за управление учётными записями")
@RequiredArgsConstructor
public class AccountsController {
    private final JwtService jwtService;
    private final AccountsService accountsService;

    // TODO: добавить пагинацию, чтобы не выгружать всю базу пользователей в память
    // TODO: рассмотреть вариант возвращать DTO вместо объекта доменной области

    @Operation(
            summary = "Получение списка всех пользователей администратором",
            description = """
                    Позволяет администратору получить список всех пользователей, зарегистрированных в сервисе.""",
            responses = {
                    @ApiResponse(description = "Список получен успешно", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному методу", responseCode = "403", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public List<User> getAllUsers() {
        return accountsService.getUsers();
    }

    // Администратор может получить данные о любом аккаунте
    // Другие пользователи - только данные о своём аккаунте
    @Operation(
            summary = "Получение учётной записи по ID",
            description = """
                    Получить данные о пользователе по ID. Администратор может получить данные о любой учётной записи, другие пользователи - только о своей.""",
            responses = {
                    @ApiResponse(description = "Пользователь получен успешно", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{user_id}")
    public User getUserById(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader, @PathVariable("user_id") Long userId) {
        return accountsService.getUserById(userId);
    }

    // Администратор может обновить данные любого аккаунта, кроме аккаунта другого администратора
    // Другие пользователи - только данные своего аккаунта
    @Operation(
            summary = "Редактирование учётной записи по ID",
            description = """
                    Редактировать данные о пользователе по ID. Администратор может редактировать данные о любой учётной записи, другие пользователи - только о своей.""",
            responses = {
                    @ApiResponse(description = "Редактирование прошло успешно", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{user_id}")
    public User updateUserById(@PathVariable("user_id") @Parameter(description = "Идентификатор пользователя") Long userId,
                               @RequestBody @Valid UpdateUserDto updateUserDto) {
        return accountsService.updateUserById(userId, updateUserDto);
    }

    @Hidden
    @Operation(
            summary = "Удаление учётной записи по ID",
            description = """
                    Удалить учётную запись по ID. Администратор может удалить любую учётную записи, другие пользователи - только свою.""",
            responses = {
                    @ApiResponse(description = "Пользователь удалён успешно", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{user_id}")
    public JSONObject deleteUser(@PathVariable("user_id") @Parameter(description = "Идентификатор пользователя") Long userId) {
        accountsService.deleteUserById(userId);
        JSONObject response = new JSONObject();
        response.put("message", "User with id " + userId + " deleted successfully");
        return response;
    }

    @Operation(
            summary = "Редактирование e-mail учётной записи по ID",
            description = """
                    Редактировать e-mail учётной записи по id. Администратор может редактировать e-mail любой учётной записи, другие пользователи - только своей.""",
            responses = {
                    @ApiResponse(description = "Редактирование прошло успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthJwtPair.class))),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{user_id}/email")
    public AuthJwtPair updateUserEmail(@PathVariable("user_id") @Parameter(description = "Идентификатор пользователя") Long userId,
                                       @RequestBody @Valid UpdateUserEmailDto updateUserEmailDto) {
        return accountsService.updateUserEmailById(userId, updateUserEmailDto);
    }

    @Operation(
            summary = "Редактирование пароля учётной записи по ID",
            description = """
                    Редактировать пароль учётной записи по id. Администратор может редактировать пароль любой учётной записи не указывая её старый пароль, другие пользователи - только своей, и с указанием старого пароля.""",
            responses = {
                    @ApiResponse(description = "Редактирование прошло успешно", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Прошлый пароль был указан неверно", responseCode = "422", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{user_id}/password")
    public User updateUserEmail(@PathVariable("user_id") @Parameter(description = "Идентификатор пользователя") Long userId,
                                @RequestBody @Valid UpdateUserPasswordDto updateUserPasswordDto) {
        return accountsService.updateUserPasswordById(userId, updateUserPasswordDto);
    }

    @Operation(
            summary = "Редактирование лимита документов студента администратором по ID",
            description = """
                    Администратор может редактировать лимит документов для любого студента.""",
            responses = {
                    @ApiResponse(description = "Редактирование прошло успешно", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{user_id}/documents-limit")
    public User updateUserEmail(@PathVariable("user_id") @Parameter(description = "Идентификатор пользователя") Long userId,
                                @RequestBody @Valid UpdateUserDocumentsLimitDto updateUserDocumentsLimitDto) {
        return accountsService.updateUserDocumentsLimitById(userId, updateUserDocumentsLimitDto);
    }

    @Hidden
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
