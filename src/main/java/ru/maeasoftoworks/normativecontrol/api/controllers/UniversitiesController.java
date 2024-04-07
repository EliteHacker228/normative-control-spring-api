package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.services.UniversitiesService;

import java.util.List;

@RestController
@RequestMapping("/universities")
@RequiredArgsConstructor
public class UniversitiesController {

    private final UniversitiesService universitiesService;

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
}
