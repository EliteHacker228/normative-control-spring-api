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
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtUtils jwtUtils;
    private final UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("1");

        // Получаем токен из заголовка
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !authHeader.toLowerCase().startsWith(BEARER_PREFIX.toLowerCase())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("2");

        // Обрезаем префикс и получаем имя пользователя из токена
        var jwt = authHeader.substring(BEARER_PREFIX.length());
        var username = jwtUtils.getClaimsFromAccessTokenString(jwt).getPayload().getSubject();

        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService()
                    .loadUserByUsername(username);

            log.info("3");

            // Если токен валиден, то аутентифицируем пользователя
            if (jwtUtils.isAccessTokenValid(jwt)) {
                log.info("4");
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
                log.info("5");
            }
        }

        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        filterChain.doFilter(request, response);
    }


    @Bean
    public UserDetailsService userDetailsService() {
//        UserDetails user =
//                User.builder()
//                        .username("inspector@urfu.ru")
//                        .password("inspector")
//                        .roles("INSPECTOR")
//                        .build();

//        log.info("IN MEM USER: " + user.toString());
//
//        return new InMemoryUserDetailsManager(user);

        return (username) -> {
            log.info("SEARCHING BY: " + username);
            var user = usersRepository.findByEmail(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            else {
                log.info("FOUND: " + user.toString());
                return User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build();
            }
        };
    }
}