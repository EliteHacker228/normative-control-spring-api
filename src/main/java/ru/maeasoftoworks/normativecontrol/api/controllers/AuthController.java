package ru.maeasoftoworks.normativecontrol.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.*;
import ru.maeasoftoworks.normativecontrol.api.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Tag(name = "Auth", description = "Отвечает за вход, регистрацию, и обновление токенов")
    @Operation(
            summary = "Вход в существующую учётную запись",
            description = "Позволяет войти в учётную запись. В ответ возвращает токены обновления и доступа для пользователя.",
            responses = {
                    @ApiResponse(description = "Вход выполнен успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthJwtPair.class))),
                    @ApiResponse(description = "Запрос на вход не корректен: одно или несколько переданных значений не валидны", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @PostMapping("/login")
    private AuthJwtPair login(@RequestBody @Valid LoginData loginData) {
        return authService.login(loginData);
    }

    @Tag(name = "Auth", description = "Отвечает за вход, регистрацию, и обновление токенов")
    @Operation(
            summary = "Регистрация учётной записи студента",
            description = "Позволяет создать учётную запись студента. E-mail должен принадлежать доменной зоне @urfu.me, @urfu.ru или @at.urfu.ru. fullName должно быть валидным ФИО, состоящим из имени, фамилии (опционально - отчества), записанными кириллицей.",
            responses = {
                    @ApiResponse(description = "Создание учётной записи выполнено успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthJwtPair.class))),
                    @ApiResponse(description = "Запрос на создание учётной записи не корректен: одно или несколько переданных значений не валидны", responseCode = "400", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Текущий e-mail уже зарегистрирован в системе", responseCode = "409", content = @Content(mediaType = "application/json")),
            })
    @PostMapping("/register/student")
    private AuthJwtPair registerStudent(@RequestBody @Valid RegisterStudentDto registerStudentDto) {
        return authService.registerStudent(registerStudentDto);
    }

    @Tag(name = "Auth", description = "Отвечает за вход, регистрацию, и обновление токенов")
    @Operation(
            summary = "Регистрация учётной записи нормоконтроёлра",
            description = "Позволяет создать учётную запись студента. E-mail должен принадлежать доменной зоне @urfu.me, @urfu.ru или @at.urfu.ru. fullName должно быть валидным ФИО, состоящим из имени, фамилии (опционально - отчества), записанными кириллицей.",
            responses = {
                    @ApiResponse(description = "Создание учётной записи выполнено успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthJwtPair.class))),
                    @ApiResponse(description = "Запрос на создание учётной записи не корректен: одно или несколько переданных значений не валидны", responseCode = "400", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Текущий e-mail уже зарегистрирован в системе", responseCode = "409", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/register/normocontroller")
    private AuthJwtPair registerNormocontroller(@RequestBody @Valid RegisterNormocontrollerDto registerNormocontrollerDto) {
        return authService.registerNormocontroller(registerNormocontrollerDto);
    }

    @Tag(name = "Auth", description = "Отвечает за вход, регистрацию, и обновление токенов")
    @Operation(
            summary = "Обновление токенов",
            description = "Позволяет получить новые токены обновления и доступа для пользователя, используя его токен обновления. При этом, используемый токен обновления будет инвалидирован",
            responses = {
                    @ApiResponse(description = "Обновление токенов прошло успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthJwtPair.class))),
                    @ApiResponse(description = "Запрос на обновление токенов не корректен: поле refreshToken отстутствует либо пустое", responseCode = "400", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Запрос на обновление токенов отклонён: refreshToken не корректен, истёк, либо инвалидирован", responseCode = "401", content = @Content(mediaType = "application/json")),
            })
    @PutMapping("/tokens")
    private AuthJwtPair updateAuthTokens(@RequestBody @Valid UpdateAuthTokensDto updateAuthTokensDto) {
        String refreshToken = updateAuthTokensDto.getRefreshToken();
        return authService.updateAuthTokens(refreshToken);
    }
}
