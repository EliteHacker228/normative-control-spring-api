package ru.maeasoftoworks.normativecontrol.api.security.accessrules;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.dto.invites.InviteDto;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.io.IOException;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvitesCreationAccessRule implements AccessRule {

    private final JwtService jwtService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext ctx) {
        try {
            return tryCheck(authentication, ctx);
        } catch (Exception e) {
            return new AuthorizationDecision(false);
        }
    }

    private AuthorizationDecision tryCheck(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) throws IOException {
        String accessToken = ctx.getRequest().getHeader("Authorization").substring(("Bearer ").length());

        ObjectMapper mapper = new ObjectMapper();
        InviteDto inviteDto = mapper.readValue(ctx.getRequest().getInputStream(), InviteDto.class);

        Jwt jwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = jwt.getUser();
        if (user.getRole() == Role.ADMIN)
            return new AuthorizationDecision(true);
        if (user.getRole() == Role.NORMOCONTROLLER && user.getId() == inviteDto.getOwnerId())
            return new AuthorizationDecision(true);
        return new AuthorizationDecision(false);
    }
}
