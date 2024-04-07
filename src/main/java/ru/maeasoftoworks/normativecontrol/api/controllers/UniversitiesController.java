package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
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
}
