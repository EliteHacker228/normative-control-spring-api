package ru.maeasoftoworks.normativecontrol.api.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.security.cachedrequest.CachedBodyHttpServletRequest;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !authHeader.toLowerCase().startsWith(BEARER_PREFIX.toLowerCase())) {
//            FIXME: Сваггер с этим не работает (что?)
//            response.setStatus(400);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(BEARER_PREFIX.length());
        String username;
        try {
            username = jwtService.getClaimsFromAccessTokenString(jwt).getPayload().getSubject();
        } catch (ExpiredJwtException e) {
            response.setStatus(403);
            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            log.info("Owner of access token is registred in DB");

            if (jwtService.isAccessTokenValid(jwt)) {
                log.info("Access token is valid");
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);
                authToken.setDetails(webAuthenticationDetails);
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
                log.info("Allowed access to endpoint {} for \"{}\" from IP = {}",
                        request.getRequestURI(),
                        userDetails.getUsername(),
                        webAuthenticationDetails.getRemoteAddress());
            }
        }

        log.debug(SecurityContextHolder.getContext().getAuthentication().getName());

        if (request.getRequestURI().contains("invites")) {
            CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                    new CachedBodyHttpServletRequest(request);

            filterChain.doFilter(cachedBodyHttpServletRequest, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}