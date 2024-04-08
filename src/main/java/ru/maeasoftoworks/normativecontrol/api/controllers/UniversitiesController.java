package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.CreateAcademicGroupDto;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.UpdateAcademicGroupDto;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.services.UniversitiesService;

import java.util.List;

@RestController
@RequestMapping("/universities")
@RequiredArgsConstructor
public class UniversitiesController {

    private final UniversitiesService universitiesService;
    private final JwtService jwtService;

    @GetMapping
    public List<University> getAllUniversities() {
        return universitiesService.getUniversities();
    }

    @PostMapping
    public ResponseEntity<JSONObject> createUniversity() {
        JSONObject response = new JSONObject();
        response.put("message", "University creation functional is not implemented yet");
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }

    @PatchMapping("/{university_id}")
    public ResponseEntity<JSONObject> updateUniversity() {
        JSONObject response = new JSONObject();
        response.put("message", "University updating functional is not implemented yet");
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }

    @DeleteMapping("/{university_id}")
    public ResponseEntity<JSONObject> deleteUniversity() {
        JSONObject response = new JSONObject();
        response.put("message", "University deletion functional is not implemented yet");
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }

    @GetMapping("/{university_id}/groups")
    public List<AcademicGroup> getAcademicGroupsOfUniversity(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable("university_id") Long universityId) {
        User user = jwtService.getUserFromAuthorizationHeader(authorizationHeader);
        University university = universitiesService.getOwnUniversity(user, universityId);
        return universitiesService.getAcademicGroupsOfUniversity(university);
    }

    @PostMapping("/{university_id}/groups")
    public AcademicGroup createAcademicGroupForUniversity(@RequestHeader("Authorization") String authorizationHeader,
                                                                @PathVariable("university_id") Long universityId,
                                                                @RequestBody CreateAcademicGroupDto createAcademicGroupDto) {
        User user = jwtService.getUserFromAuthorizationHeader(authorizationHeader);
        University university = universitiesService.getOwnUniversity(user, universityId);
        return universitiesService.createAcademicGroupForUniversity(university, createAcademicGroupDto);
    }

    // TODO: universitiesService.getOwnUniversity получает данные, а если они недоступны - кидает исключение.
    // TODO: Подумать, как это можно разделить на 2 сущности
    @GetMapping("/{university_id}/groups/{group_id}")
    public AcademicGroup getAcademicGroupForUniversityById(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable("university_id") Long universityId,
                                                          @PathVariable("group_id") Long academicGroupId) {
        User user = jwtService.getUserFromAuthorizationHeader(authorizationHeader);
        University university = universitiesService.getOwnUniversity(user, universityId);
        return universitiesService.getOwnAcademicGroup(user, academicGroupId);
    }

    @PatchMapping("/{university_id}/groups/{group_id}")
    public AcademicGroup createAcademicGroupForUniversity(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable("university_id") Long universityId,
                                                          @PathVariable("group_id") Long academicGroupId,
                                                          @RequestBody UpdateAcademicGroupDto updateAcademicGroupDto) {
        User user = jwtService.getUserFromAuthorizationHeader(authorizationHeader);
        University university = universitiesService.getOwnUniversity(user, universityId);
        AcademicGroup academicGroup = universitiesService.getOwnAcademicGroup(user, academicGroupId);
        return universitiesService.updateAcademicGroupForUniversity(academicGroup, updateAcademicGroupDto);
    }
}
