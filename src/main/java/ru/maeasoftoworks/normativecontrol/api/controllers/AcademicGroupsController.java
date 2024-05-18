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
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.CreateAcademicGroupDto;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.NormocontrollerDto;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.UpdateAcademicGroupDto;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.services.AcademicalService;

import java.text.MessageFormat;
import java.util.List;

@RestController
@RequestMapping("/academical")
@RequiredArgsConstructor
@Tag(name = "Academic groups", description = "Отвечает за управление академическими группами и их нормоконтролёрами")
public class AcademicGroupsController {

    private final AcademicalService academicalService;
    private final JwtService jwtService;

    @Operation(
            summary = "Получение списка всех нормоконтролёров",
            description = """
                    Позволяет пользователю получить список всех нормоконтролёров""",
            responses = {
                    @ApiResponse(description = "Список получен успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AcademicGroup.class))),
            })
    @GetMapping("/normocontrollers")
    public List<NormocontrollerDto> getNormocontrollers() {
        return academicalService.getNormocontrollers()
                .stream()
                .map(normocontroller -> NormocontrollerDto.builder()
                        .id(normocontroller.getId())
                        .fullName(normocontroller.getFullName())
                        .build())
                .toList();
    }

    @Operation(
            summary = "Получение списка всех академических групп",
            description = """
                    Позволяет пользователю получить список всех академических групп""",
            responses = {
                    @ApiResponse(description = "Список получен успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AcademicGroup.class))),
            })
    @GetMapping("/groups")
    public List<AcademicGroup> getAcademicGroups() {
        return academicalService.getAcademicGroups();
    }

    @Operation(
            summary = "Создание новой академической группы администратором",
            description = """
                    Позволяет администратору создать новую академическую группу""",
            responses = {
                    @ApiResponse(description = "Группа создана успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AcademicGroup.class))),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/groups")
    public AcademicGroup createAcademicGroup(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                             @RequestBody @Valid CreateAcademicGroupDto createAcademicGroupDto) {
        User user = jwtService.getUserFromAuthorizationHeader(authorizationHeader);
        return academicalService.createAcademicGroup(createAcademicGroupDto);
    }

    @Operation(
            summary = "Получение данных о академической группе",
            description = """
                    Позволяет пользователю получить данные о академической группе по её ID""",
            responses = {
                    @ApiResponse(description = "Данные получены успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AcademicGroup.class))),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @GetMapping("/groups/{group_id}")
    public AcademicGroup getAcademicGroupById(@PathVariable("group_id") @Parameter(description = "Идентификатор академической группы") Long academicGroupId) {
        return academicalService.getAcademicalGroupById(academicGroupId);
    }

    @Operation(
            summary = "Изменение информации о академической группе",
            description = """
                    Позволяет администратору изменить данные о академической группе""",
            responses = {
                    @ApiResponse(description = "Данные изменены успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AcademicGroup.class))),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/groups/{group_id}")
    public AcademicGroup createAcademicGroup(@PathVariable("group_id") @Parameter(description = "Идентификатор академической группы") Long academicGroupId,
                                             @RequestBody @Valid UpdateAcademicGroupDto updateAcademicGroupDto) {
        return academicalService.updateAcademicGroupById(academicGroupId, updateAcademicGroupDto);
    }

    @DeleteMapping("/groups/{group_id}")
    public ResponseEntity<JSONObject> deleteAcademicGroup(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable("group_id") Long academicGroupId) {
        User user = jwtService.getUserFromAuthorizationHeader(authorizationHeader);
        academicalService.deleteAcademicGroupById(academicGroupId);

        JSONObject response = new JSONObject();
        response.put("message", MessageFormat.format("Academic group with id {0} deleted successfully", academicGroupId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(
            summary = "Получение информации о студентах из академической группы",
            description = """
                    Позволяет нормоконтролёру получить данные о студентах из группы по её ID, если он к ней приписан.
                     Позволяет администратору получить данные о студентах из любой группы по её ID""",
            responses = {
                    @ApiResponse(description = "Данные получены успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Student.class))),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступа к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/groups/{group_id}/students")
    public List<Student> getStudentsFromAcademicGroup(@PathVariable("group_id") Long academicGroupId) {
        return academicalService.getStudentsFromAcademicGroup(academicGroupId);
    }
}
