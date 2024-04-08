package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.VerificationStatus;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.CreateDocumentDto;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.DocumentVerdictDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.UnauthorizedException;
import ru.maeasoftoworks.normativecontrol.api.services.DocumentsService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentsController {
    private final DocumentsService documentsService;
    private final JwtService jwtService;

    @GetMapping
    public List<Document> getDocuments(@RequestHeader("Authorization") String bearerToken) {
        Admin admin = (Admin) jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.getDocuments(admin);
    }

    @PostMapping
    public Result createDocument(@RequestHeader("Authorization") String bearerToken,
                                 @ModelAttribute CreateDocumentDto createDocumentDto) {
        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        // TODO: Перенести эту логику в Spring Security
        if (user.getRole() == Role.ADMIN)
            throw new UnauthorizedException("Admin can not send documents to verification");
        return documentsService.createDocument(user, createDocumentDto);
    }

    @DeleteMapping("/{document_id}")
    public ResponseEntity<JSONObject> deleteDocument(@RequestHeader("Authorization") String bearerToken,
                                                     @PathVariable("document_id") Long documentId) {
        Admin admin = (Admin) jwtService.getUserFromAuthorizationHeader(bearerToken);
        documentsService.deleteDocument(admin, documentId);
        JSONObject response = new JSONObject();
        response.put("message", "Document with id " + documentId + " deleted successfully");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{document_id}/status")
    public ResponseEntity<JSONObject> getDocumentsVerificationStatus(@RequestHeader("Authorization") String bearerToken,
                                                                     @PathVariable("document_id") Long documentId) {
        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        VerificationStatus status = documentsService.getDocumentsVerificationStatus(documentId);
        JSONObject response = new JSONObject();
        response.put("status", status.name());
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{document_id}")
    public Document setVerdictOnDocument(@RequestHeader("Authorization") String bearerToken,
                                       @PathVariable("document_id") Long documentId,
                                       @RequestBody DocumentVerdictDto documentVerdictDto) {
        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.setVerdictOnDocument(documentId, documentVerdictDto);
    }
}
