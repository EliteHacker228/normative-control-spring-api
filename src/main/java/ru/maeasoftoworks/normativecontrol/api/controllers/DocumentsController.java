package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.CreateDocumentDto;
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
        if(user.getRole() == Role.ADMIN)
            throw new UnauthorizedException("Admin can not send documents to verification");
        return documentsService.createDocument(user, createDocumentDto);
    }
}
