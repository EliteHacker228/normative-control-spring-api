package ru.maeasoftoworks.normativecontrol.api.controllers;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invites")
public class InvitesController {

    @GetMapping
    public ResponseEntity<JSONObject> getInvites() {
        JSONObject response = new JSONObject();
        response.put("message", "Invites getting functional is not implemented yet");
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<JSONObject> createInvite() {
        JSONObject response = new JSONObject();
        response.put("message", "Invites creation functional is not implemented yet");
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }

    @DeleteMapping("/{invite_id}")
    public ResponseEntity<JSONObject> deleteInvite() {
        JSONObject response = new JSONObject();
        response.put("message", "Invites deletion functional is not implemented yet");
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }
}
