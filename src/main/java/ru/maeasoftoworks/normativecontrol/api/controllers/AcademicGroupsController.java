package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
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
public class AcademicGroupsController {

    private final AcademicalService academicalService;
    private final JwtService jwtService;

    @GetMapping("/normocontrollers")
    public List<NormocontrollerDto> getNormocontrollers() {
        return academicalService.getNormocontrollers()
                .stream()
                .map(normocontroller -> NormocontrollerDto.builder()
                        .id(normocontroller.getId())
                        .firstName(normocontroller.getFirstName())
                        .middleName(normocontroller.getMiddleName())
                        .lastName(normocontroller.getLastName())
                        .build())
                .toList();
    }

    @GetMapping("/groups")
    public List<AcademicGroup> getAcademicGroups() {
        return academicalService.getAcademicGroups();
    }

    @PostMapping("/groups")
    public AcademicGroup createAcademicGroup(@RequestHeader("Authorization") String authorizationHeader,
                                             @RequestBody CreateAcademicGroupDto createAcademicGroupDto) {
        User user = jwtService.getUserFromAuthorizationHeader(authorizationHeader);
        return academicalService.createAcademicGroup(createAcademicGroupDto);
    }

    // TODO: universitiesService.getOwnUniversity получает данные, а если они недоступны - кидает исключение.
    // TODO: Подумать, как это можно разделить на 2 сущности
    @GetMapping("/groups/{group_id}")
    public AcademicGroup getAcademicGroupById(@PathVariable("group_id") Long academicGroupId) {
        return academicalService.getAcademicalGroupById(academicGroupId);
    }

    @PatchMapping("/groups/{group_id}")
    public AcademicGroup createAcademicGroup(@PathVariable("group_id") Long academicGroupId,
                                             @RequestBody UpdateAcademicGroupDto updateAcademicGroupDto) {
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

    @GetMapping("/groups/{group_id}/students")
    public List<Student> getStudentsFromAcademicGroup(@PathVariable("group_id") Long academicGroupId) {
        return academicalService.getStudentsFromAcademicGroup(academicGroupId);
    }
}
