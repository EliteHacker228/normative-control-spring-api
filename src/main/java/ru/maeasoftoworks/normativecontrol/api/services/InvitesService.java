package ru.maeasoftoworks.normativecontrol.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.invites.Invite;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.dto.invites.InviteDto;
import ru.maeasoftoworks.normativecontrol.api.repositories.AcademicGroupsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.InvitesRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.NormocontrollersRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InvitesService {

    private final InvitesRepository invitesRepository;
    private final NormocontrollersRepository normocontrollersRepository;
    private final AcademicGroupsRepository academicGroupsRepository;

    public Invite createInviteAsNormocontrollerFromInviteDto(Normocontroller normocontroller, InviteDto inviteDto) {
        AcademicGroup academicGroup = academicGroupsRepository.findAcademicGroupById(inviteDto.getAcademicGroupId());

        Invite invite = Invite.builder()
                .owner(normocontroller)
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
