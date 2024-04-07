package ru.maeasoftoworks.normativecontrol.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.repositories.UniversitiesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversitiesService {
    private final UniversitiesRepository universitiesRepository;

    public List<University> getUniversities(){
        return universitiesRepository.findAll();
    }
}
