package ru.maeasoftoworks.normativecontrol.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Admin;
import ru.maeasoftoworks.normativecontrol.api.services.AccountsService;
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
}
