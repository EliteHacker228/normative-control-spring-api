package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.CreateAcademicGroupDto;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.UpdateAcademicGroupDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceDoesNotExists;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UnauthorizedException;
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

    @Transactional
    public AcademicGroup createAcademicGroupForUniversity(University university, CreateAcademicGroupDto createAcademicGroupDto) {
        AcademicGroup academicGroup = new AcademicGroup(university, createAcademicGroupDto.getName());
        academicGroupsRepository.save(academicGroup);
        return academicGroup;
    }

    public AcademicGroup getOwnAcademicGroup(User actor, Long targetId) {
        AcademicGroup target = academicGroupsRepository.findAcademicGroupById(targetId);
        if (actor.getUniversity() != target.getUniversity())
            throw new UnauthorizedException("You are not authorized to access this resource");
        return target;
    }

    @Transactional
    public AcademicGroup updateAcademicGroupForUniversity(AcademicGroup academicGroup, UpdateAcademicGroupDto updateAcademicGroupDto) {
        academicGroup.setName(updateAcademicGroupDto.getName());
        academicGroupsRepository.save(academicGroup);
        return academicGroup;
    }
}
