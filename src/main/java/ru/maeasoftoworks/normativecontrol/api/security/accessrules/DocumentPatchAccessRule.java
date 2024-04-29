package ru.maeasoftoworks.normativecontrol.api.security.accessrules;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.services.DocumentsService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.util.function.Supplier;

// Доступ только нормококонтролёру
@Component
@RequiredArgsConstructor
public class DocumentPatchAccessRule implements AccessRule {

    private final JwtService jwtService;
    private final DocumentsService documentsService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext ctx) {
        Long documentId = Long.parseLong(ctx.getVariables().get("document_id"));

        String accessToken = ctx.getRequest().getHeader("Authorization").substring(("Bearer ").length());
        Jwt jwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = jwt.getUser();

        Document document = documentsService.getDocumentNode(user, documentId);

        if (user.getRole() == Role.NORMOCONTROLLER && user.getId() == document.getStudent().getAcademicGroup().getNormocontroller().getId())
            return new AuthorizationDecision(true);

        return new AuthorizationDecision(false);
    }
}
