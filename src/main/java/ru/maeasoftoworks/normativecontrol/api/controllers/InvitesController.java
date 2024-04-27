package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.invites.Invite;
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
    public Invite createInvite(@RequestBody InviteDto inviteDto) {
        return invitesService.createInviteFromInviteDto(inviteDto);
    }

    @DeleteMapping("/{invite_id}")
    public ResponseEntity<JSONObject> deleteInvite(@PathVariable("invite_id") Integer inviteId) {
        invitesService.deleteInviteById(inviteId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Invite with id " + inviteId + " deleted successfully");

        return ResponseEntity.ok().body(jsonObject);
    }
}
