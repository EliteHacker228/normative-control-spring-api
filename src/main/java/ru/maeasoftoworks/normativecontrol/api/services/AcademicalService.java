package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.CreateAcademicGroupDto;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.UpdateAcademicGroupDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceNotFoundException;
import ru.maeasoftoworks.normativecontrol.api.repositories.AcademicGroupsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.NormocontrollersRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.StudentsRepository;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicalService {
    private final AcademicGroupsRepository academicGroupsRepository;
    private final StudentsRepository studentsRepository;
    private final NormocontrollersRepository normocontrollersRepository;

    // Доступна всем
    public List<AcademicGroup> getAcademicGroups() {
        return academicGroupsRepository.findAll();
    }

    // Доступна всем
    public AcademicGroup getAcademicalGroupById(Long academicGroupId) {
        AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(academicGroupId);
        if (academicGroup == null) {
            String message = MessageFormat.format("Academic group with id {0} not found", academicGroupId);
            throw new ResourceNotFoundException(message);
        }
        return academicGroup;
    }

    // Доступна всем
    public List<Normocontroller> getNormocontrollers() {
        return normocontrollersRepository.findAll();
    }

    // Доступна админам
    @Transactional
    public AcademicGroup createAcademicGroup(CreateAcademicGroupDto createAcademicGroupDto) {
        AcademicGroup academicGroup = new AcademicGroup(createAcademicGroupDto.getName());
        academicGroupsRepository.save(academicGroup);
        return academicGroup;
    }

    // Доступна админам
    @Transactional
    public AcademicGroup updateAcademicGroupById(Long academicGroupId, UpdateAcademicGroupDto updateAcademicGroupDto) {
        AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(academicGroupId);
        if (academicGroup == null) {
            String message = MessageFormat.format("Academic group with id {0} not found", academicGroupId);
            throw new ResourceNotFoundException(message);
        }
        academicGroup.setName(updateAcademicGroupDto.getName());
        academicGroupsRepository.save(academicGroup);
        return academicGroup;
    }

    // Доступна админам
    @Transactional
    public void deleteAcademicGroup(AcademicGroup academicGroup) {
        academicGroupsRepository.delete(academicGroup);
    }

    // Доступна админам
    @Transactional
    public void deleteAcademicGroupById(Long academicGroupId) {
        AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(academicGroupId);
        academicGroupsRepository.delete(academicGroup);
    }

    // Доступна админам и нормоконтролёрам
    public List<Student> getStudentsFromAcademicGroup(Long academicGroupId) {
        AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(academicGroupId);
        if (academicGroup == null)
            throw new ResourceNotFoundException("Academic group with id " + academicGroupId + "does not exists");
        return studentsRepository.findStudentsByAcademicGroup(academicGroup);
    }
}
