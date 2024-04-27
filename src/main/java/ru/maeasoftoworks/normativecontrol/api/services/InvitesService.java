package ru.maeasoftoworks.normativecontrol.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.invites.Invite;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.dto.invites.InviteDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceNotFoundException;
import ru.maeasoftoworks.normativecontrol.api.repositories.AcademicGroupsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.InvitesRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.NormocontrollersRepository;

import java.text.MessageFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InvitesService {

    private final InvitesRepository invitesRepository;
    private final NormocontrollersRepository normocontrollersRepository;
    private final AcademicGroupsRepository academicGroupsRepository;

    public Invite createInviteFromInviteDto(InviteDto inviteDto) {
        Normocontroller owner = normocontrollersRepository.findNormocontrollerById(inviteDto.getOwnerId());
        if (owner == null) {
            String message = MessageFormat.format("Normocontroller with id {0} not found", inviteDto.getOwnerId());
            throw new ResourceNotFoundException(message);
        }

        AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(inviteDto.getAcademicGroupId());
        if (academicGroup == null) {
            String message = MessageFormat.format("Academic group with id {0} not found", inviteDto.getAcademicGroupId());
            throw new ResourceNotFoundException(message);
        }

        Invite invite = Invite.builder()
                .owner(owner)
                .academicGroup(academicGroup)
                .createdAt(inviteDto.getCreatedAt())
                .expiresAt(inviteDto.getExpiresAt())
                .build();

        return invitesRepository.save(invite);
    }

    public List<Invite> getInvites() {
        return invitesRepository.findAll();
    }

    public List<Invite> getInvitesForNormocontroller(Normocontroller normocontroller) {
        return invitesRepository.findAllByOwner(normocontroller);
    }

    public Invite getInviteById(Integer inviteId) {
        return invitesRepository.findById(inviteId).orElse(null);
    }

    public void deleteInviteById(Integer inviteId) {
        invitesRepository.deleteById(inviteId);
    }
}
