package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.CreateDocumentDto;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.DocumentReportDto;
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
        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.getDocuments(user);
    }

    @GetMapping("/actual")
    public List<Document> getActualDocuments(@RequestHeader("Authorization") String bearerToken) {
        Normocontroller normocontroller = (Normocontroller) jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.getActualDocuments(normocontroller);
    }

    @GetMapping("/csv")
    public String getDocumentsCsv(@RequestHeader("Authorization") String bearerToken) {
        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.getDocumentsCsv(user);
    }

    @PostMapping
    public Document createDocument(@RequestHeader("Authorization") String bearerToken,
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
    public Result getDocumentVerificationStatus(@RequestHeader("Authorization") String bearerToken,
                                                @PathVariable("document_id") Long documentId) {
        return documentsService.getDocumentVerificationStatus(documentId);
    }

    @GetMapping("/{document_id}")
    @SneakyThrows
    public ResponseEntity getDocument(@RequestHeader("Authorization") String bearerToken,
                                              @PathVariable("document_id") Long documentId,
                                              @RequestParam(name = "type") String documentType) {

        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        if(documentType.equals("node"))
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(documentsService.getDocumentNode(user, documentId));

        byte[] documentBytes = documentsService.getDocument(user, documentId, documentType);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(documentBytes);
    }


    @PatchMapping("/{document_id}")
    public Document setVerdictOnDocument(@RequestHeader("Authorization") String bearerToken,
                                         @PathVariable("document_id") Long documentId,
                                         @RequestBody DocumentVerdictDto documentVerdictDto) {
        return documentsService.setVerdictOnDocument(documentId, documentVerdictDto);
    }

    @PostMapping("/{document_id}/report")
    public Document reportOnDocument(@RequestHeader("Authorization") String bearerToken,
                                     @PathVariable("document_id") Long documentId,
                                     @RequestBody DocumentReportDto documentReportDto) {
        return documentsService.reportOnDocument(documentId, documentReportDto);
    }

    @DeleteMapping("/{document_id}/report")
    public Document unreportOnDocument(@RequestHeader("Authorization") String bearerToken,
                                     @PathVariable("document_id") Long documentId,
                                     @RequestBody DocumentReportDto documentReportDto) {
        return documentsService.unreportOnDocument(documentId, documentReportDto);
    }

    @PostMapping("/{document_id}/verdict")
    public Document makeVerdict(@RequestHeader("Authorization") String bearerToken,
                                @PathVariable("document_id") Long documentId,
                                @RequestBody DocumentVerdictDto documentVerdictDto) {
        return documentsService.makeVerdictOnDocument(documentId, documentVerdictDto);
    }
}
