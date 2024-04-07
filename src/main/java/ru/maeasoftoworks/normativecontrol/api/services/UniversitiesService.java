package ru.maeasoftoworks.normativecontrol.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UnauthorizedException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserDoesNotExistsException;
import ru.maeasoftoworks.normativecontrol.api.repositories.AcademicGroupsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UniversitiesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversitiesService {
    private final UniversitiesRepository universitiesRepository;
    private final AcademicGroupsRepository academicGroupsRepository;

    public List<University> getUniversities() {
        return universitiesRepository.findAll();
    }

    public University getOwnUniversity(User actor, Long targetId) {
        University target = universitiesRepository.findUniversityById(targetId);
        if (actor.getUniversity() != target)
            throw new UnauthorizedException("You are not authorized to access this resource");
        return target;
    }

    public List<AcademicGroup> getAcademicGroupsOfUniversity(University university) {
        return academicGroupsRepository.findAcademicGroupsByUniversityId(university.getId());
    }
}
