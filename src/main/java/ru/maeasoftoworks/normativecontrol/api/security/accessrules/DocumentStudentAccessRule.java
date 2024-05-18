package ru.maeasoftoworks.normativecontrol.api.security.accessrules;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Student;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.services.AccountsService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DocumentStudentAccessRule implements AccessRule {

    private final JwtService jwtService;
    private final AccountsService accountsService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext ctx) {
        try {
            return tryCheck(authentication, ctx);
        } catch (Exception e) {
            return new AuthorizationDecision(false);
        }
    }

    private AuthorizationDecision tryCheck(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        Long studentId = Long.parseLong(ctx.getVariables().get("student_id"));

        String accessToken = ctx.getRequest().getHeader("Authorization").substring(("Bearer ").length());
        Jwt jwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = jwt.getUser();

        User targetUser = accountsService.getUserById(studentId);

        if (user.getRole() == Role.ADMIN && targetUser.getRole() == Role.STUDENT)
            return new AuthorizationDecision(true);
        if (user.getRole() == Role.STUDENT && targetUser.getRole() == Role.STUDENT && user.getId() == targetUser.getId())
            return new AuthorizationDecision(true);
        if (user.getRole() == Role.NORMOCONTROLLER && targetUser.getRole() == Role.STUDENT && user.getId() == ((Student) targetUser).getAcademicGroup().getNormocontroller().getId())
            return new AuthorizationDecision(true);

        return new AuthorizationDecision(false);
    }
}
