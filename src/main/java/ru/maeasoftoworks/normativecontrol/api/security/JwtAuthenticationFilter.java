package ru.maeasoftoworks.normativecontrol.api.security;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("Request in filter");


        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !authHeader.toLowerCase().startsWith(BEARER_PREFIX.toLowerCase())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Request has Bearer access token");

        var jwt = authHeader.substring(BEARER_PREFIX.length());
        var username = jwtService.getClaimsFromAccessTokenString(jwt).getPayload().getSubject();

        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService()
                    .loadUserByUsername(username);

            log.info("Owner of access token is registred in DB");

            if (jwtService.isAccessTokenValid(jwt)) {
                log.info("Access token is valid");
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
                log.info(SecurityContextHolder.getContext().toString());
                log.info(authToken.toString());
                log.info("Access token passed JwtAuthenticationFilter");
            }
        }

        log.info(SecurityContextHolder.getContext().getAuthentication().getName());

        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                new CachedBodyHttpServletRequest(request);

        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }


    @Bean
    public UserDetailsService userDetailsService() {

        return (username) -> {
            log.info("SEARCHING BY: " + username);
            var user = usersRepository.findUserByEmail(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            else {
                log.info("FOUND: " + user);
                return User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build();
            }
        };
    }
}