package ru.maeasoftoworks.normativecontrol.api.security.accessrules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountsAccessRule implements AccessRule {

    private final JwtService jwtService;
    private final UsersRepository usersRepository;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext ctx) {
        try {
            return tryCheck(authentication, ctx);
        } catch (Exception e) {
            return new AuthorizationDecision(false);
        }
    }

    private AuthorizationDecision tryCheck(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        Long accountId = Long.parseLong(ctx.getVariables().get("account_id"));
        String accessToken = ctx.getRequest().getHeader("Authorization").substring(("Bearer ").length());
        Jwt jwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = jwt.getUser();

        User targetUser = usersRepository.findUsersById(accountId);
        if (user.getRole() == Role.ADMIN && targetUser != null && targetUser.getRole() != Role.ADMIN)
            return new AuthorizationDecision(true);
        if (user.getRole() == Role.ADMIN && targetUser != null && targetUser.getRole() == Role.ADMIN && user.getId() == accountId)
            return new AuthorizationDecision(true);
        if (user.getId() == accountId)
            return new AuthorizationDecision(true);
        return new AuthorizationDecision(false);
    }
}
