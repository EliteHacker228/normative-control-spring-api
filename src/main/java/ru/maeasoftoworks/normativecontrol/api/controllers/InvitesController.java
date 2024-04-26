package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.invites.Invite;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.invites.InviteDto;
import ru.maeasoftoworks.normativecontrol.api.services.InvitesService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;

import java.util.List;

@RestController
@RequestMapping("/invites")
@RequiredArgsConstructor
public class InvitesController {

    private final JwtService jwtService;
    private final InvitesService invitesService;

    @GetMapping
    public List<Invite> getInvites() {
        return invitesService.getInvites();
    }

    @GetMapping("/{invite_id}")
    public Invite getInviteById(@PathVariable("invite_id") Integer inviteId) {
        return invitesService.getInviteById(inviteId);
    }

    @PostMapping
    public Invite createInvite(@RequestHeader("Authorization") String bearerToken, @RequestBody InviteDto inviteDto) {
        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        if (user.getRole() != Role.NORMOCONTROLLER)
            throw new RuntimeException("Unauthorized");
        return invitesService.createInviteAsNormocontrollerFromInviteDto((Normocontroller) user, inviteDto);
    }

    @DeleteMapping("/{invite_id}")
    public ResponseEntity<JSONObject> deleteInvite(@PathVariable("invite_id") Integer inviteId) {
        invitesService.deleteInviteById(inviteId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Invite with id " + inviteId + " deleted successfully");

        return ResponseEntity.ok().body(jsonObject);
    }
}
