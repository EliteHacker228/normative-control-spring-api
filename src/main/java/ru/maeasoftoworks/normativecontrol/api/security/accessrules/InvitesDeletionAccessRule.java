package ru.maeasoftoworks.normativecontrol.api.security.accessrules;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.invites.Invite;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.invites.InviteDto;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceNotFoundException;
import ru.maeasoftoworks.normativecontrol.api.services.InvitesService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.text.MessageFormat;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvitesDeletionAccessRule implements AccessRule {

    private final JwtService jwtService;
    private final InvitesService invitesService;

    @Override
    @SneakyThrows
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        Integer inviteId = Integer.parseInt(ctx.getVariables().get("invite_id"));
        Invite invite = invitesService.getInviteById(inviteId);
        if (invite == null) {
            String message = MessageFormat.format("Invite with id {0} not found", inviteId);
            throw new ResourceNotFoundException(message);
        }

        String accessToken = ctx.getRequest().getHeader("Authorization").substring(("Bearer ").length());

        Jwt jwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = jwt.getUser();
        if(user.getRole() == Role.ADMIN)
            return new AuthorizationDecision(true);
        if(user.getRole() == Role.NORMOCONTROLLER && user.getId() == invite.getOwner().getId())
            return new AuthorizationDecision(true);
        return new AuthorizationDecision(false);
    }
}
