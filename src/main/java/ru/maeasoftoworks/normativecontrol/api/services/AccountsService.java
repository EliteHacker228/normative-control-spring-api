package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceDoesNotExists;
import ru.maeasoftoworks.normativecontrol.api.repositories.AcademicGroupsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.NormocontrollersRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UniversitiesRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;

@Service
@RequiredArgsConstructor
public class AccountsService {
    private final UsersRepository usersRepository;
    private final UniversitiesRepository universitiesRepository;
    private final AcademicGroupsRepository academicGroupsRepository;
    private final NormocontrollersRepository normocontrollersRepository;

    @Transactional
    public User updateUser(User user, UpdateUserDto updateUserDto) {
        user.setFirstName(updateUserDto.getFirstName());
        user.setMiddleName(updateUserDto.getMiddleName());
        user.setLastName(updateUserDto.getLastName());

        if (user.getUniversity().getId() != updateUserDto.getUniversityId()) {
            University university = universitiesRepository.findUniversityById(updateUserDto.getUniversityId());
            if (university == null)
                throw new ResourceDoesNotExists("University with id " + updateUserDto.getUniversityId()
                        + " does not exists");
            user.setUniversity(university);
        }

        if (user.getRole() == Role.STUDENT) {
            Student student = (Student) user;

            if (student.getAcademicGroup().getId() != updateUserDto.getAcademicGroupId()) {
                AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(updateUserDto.getAcademicGroupId());
                if (academicGroup == null)
                    throw new ResourceDoesNotExists("Academic group with id " + updateUserDto.getUniversityId()
                            + " does not exists");
                student.setAcademicGroup(academicGroup);
            }

            if (student.getNormocontroller().getId() != updateUserDto.getNormocontrollerId()) {
                Normocontroller normocontroller = normocontrollersRepository.findNormocontrollerById(updateUserDto.getNormocontrollerId());
                if (normocontroller == null)
                    throw new ResourceDoesNotExists("Normocontroller with id " + updateUserDto.getUniversityId()
                            + " does not exists");
                student.setNormocontroller(normocontroller);
            }

            usersRepository.save(student);
            return student;
        }

        return user;
    }

    @Transactional
    public void deleteUser(User user){
        usersRepository.delete(user);
    }
}
