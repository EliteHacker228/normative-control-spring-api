package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.University;
import ru.maeasoftoworks.normativecontrol.api.domain.users.*;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDocumentsLimitDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserEmailDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserPasswordDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.FieldNotPresents;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceDoesNotExists;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UnauthorizedException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UserDoesNotExistsException;
import ru.maeasoftoworks.normativecontrol.api.repositories.AcademicGroupsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.NormocontrollersRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UniversitiesRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountsService {
    private final UsersRepository usersRepository;
    private final UniversitiesRepository universitiesRepository;
    private final AcademicGroupsRepository academicGroupsRepository;
    private final NormocontrollersRepository normocontrollersRepository;

    public List<User> getUsersForAdmin(Admin admin) {
        List<User> foundUsers = usersRepository.findUsersByUniversity(admin.getUniversity());
        return foundUsers.stream().filter(user -> user.getRole() != Role.ADMIN || user == admin).toList();
    }

    public User getOwnUserOrAnyAsAdminById(User actor, Long targetId) {
        User target = usersRepository.findUsersById(targetId);

        if (target == null)
            throw new UserDoesNotExistsException("User with id " + targetId + " does not exists");
        if (target.getUniversity() != actor.getUniversity())
            throw new UnauthorizedException("You are not authorized to access this resource");
        if (actor.getRole() != Role.ADMIN && actor != target)
            throw new UnauthorizedException("You are not authorized to access this resource");
        if (actor.getRole() == Role.ADMIN && target.getRole() == Role.ADMIN && actor != target)
            throw new UnauthorizedException("You are not authorized to access this resource");

        return target;
    }

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
    public void deleteUser(User user) {
        usersRepository.delete(user);
    }

    @Transactional
    public User updateUserEmail(User user, UpdateUserEmailDto updateUserEmailDto) {
        user.setEmail(updateUserEmailDto.getEmail());
        usersRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUserPassword(User user, UpdateUserPasswordDto updateUserPasswordDto) {
        user.setPassword(updateUserPasswordDto.getPassword());
        usersRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUserDocumentsLimit(User user, UpdateUserDocumentsLimitDto updateUserDocumentsLimitDto) {
        if (user.getRole() == Role.STUDENT) {
            Student student = (Student) user;
            student.setDocumentsLimit(updateUserDocumentsLimitDto.getDocumentsLimit());
            usersRepository.save(student);
            return student;
        } else if (user.getRole() == Role.NORMOCONTROLLER) {
            Normocontroller normocontroller = (Normocontroller) user;
            normocontroller.setDocumentsLimit(updateUserDocumentsLimitDto.getDocumentsLimit());
            usersRepository.save(normocontroller);
            return normocontroller;
        } else {
            throw new FieldNotPresents("This user doesn't have «documents limit» field");
        }
    }
}
