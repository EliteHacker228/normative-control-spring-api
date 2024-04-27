package ru.maeasoftoworks.normativecontrol.api.security.accessrules;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public interface AccessRule extends AuthorizationManager<RequestAuthorizationContext> {
}
