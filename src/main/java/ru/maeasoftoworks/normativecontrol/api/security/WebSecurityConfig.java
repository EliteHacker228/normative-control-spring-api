package ru.maeasoftoworks.normativecontrol.api.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.security.accessrules.*;
import ru.maeasoftoworks.normativecontrol.api.utils.hashing.Sha256;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    private final AccountsAccessRule accountsAccessRule;
    private final InvitesCreationAccessRule invitesCreationAccessRule;
    private final InvitesDeletionAccessRule invitesDeletionAccessRule;

    private final DocumentAccessRule documentAccessRule;
    private final DocumentPatchAccessRule documentPatchAccessRule;
    private final DocumentReportAccessRule documentReportAccessRule;
    private final DocumentVerdictAccessRule documentVerdictAccessRule;
    private final DocumentStudentAccessRule documentStudentAccessRule;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register/normocontroller").hasRole(Role.ADMIN.name())
                        .requestMatchers("/auth/tokens").permitAll()
                        .requestMatchers("/auth/**").anonymous()

                        .requestMatchers("/accounts").hasRole(Role.ADMIN.name())
                        .requestMatchers("/accounts/*/documents-limit").hasRole(Role.ADMIN.name())
                        .requestMatchers("/accounts/{account_id}/**").access(accountsAccessRule)
                        .requestMatchers("/accounts/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/academical/groups").permitAll()
                        .requestMatchers(HttpMethod.POST, "/academical/groups").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, "/academical/groups").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/academical/groups").hasRole(Role.ADMIN.name())
                        .requestMatchers("/academical/groups/{group_id}/students").hasAnyRole(Role.NORMOCONTROLLER.name(), Role.ADMIN.name())
                        .requestMatchers("/academical/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/invites").permitAll()
                        .requestMatchers(HttpMethod.POST, "/invites").access(invitesCreationAccessRule)
                        .requestMatchers(HttpMethod.GET, "/invites/{invite_id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/invites/{invite_id}").access(invitesDeletionAccessRule)

                        .requestMatchers(HttpMethod.POST, "/documents").hasRole(Role.STUDENT.name())
                        .requestMatchers(HttpMethod.GET, "/documents").authenticated()
                        .requestMatchers(HttpMethod.GET, "/documents/all").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/documents/actual").hasAnyRole(Role.NORMOCONTROLLER.name())
                        .requestMatchers(HttpMethod.GET, "/documents/csv").hasAnyRole(Role.NORMOCONTROLLER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/documents").hasRole(Role.STUDENT.name())
                        .requestMatchers(HttpMethod.GET, "/documents").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/documents/students/{student_id}").access(documentStudentAccessRule)
                        .requestMatchers(HttpMethod.GET, "/documents/{document_id}").access(documentAccessRule)
                        .requestMatchers(HttpMethod.DELETE, "/documents/{document_id}").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/documents/{document_id}/status").access(documentAccessRule)
                        .requestMatchers(HttpMethod.PATCH, "/documents/{document_id}").access(documentPatchAccessRule)
                        .requestMatchers(HttpMethod.POST,"/documents/{document_id}/report").access(documentReportAccessRule)
                        .requestMatchers(HttpMethod.DELETE,"/documents/{document_id}/report").access(documentReportAccessRule)
                        .requestMatchers(HttpMethod.POST,"/documents/{document_id}/verdict").access(documentVerdictAccessRule)
                        .requestMatchers(HttpMethod.DELETE,"/documents/{document_id}/verdict").access(documentVerdictAccessRule)
                        .requestMatchers("/documents/**").authenticated()

                        .requestMatchers("/error", "/error/**").permitAll())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // TODO: Использовать поставляемый спрингом энкодер, а не собтсвенный через sha256
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return Sha256.getStringSha256(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return Sha256.getStringSha256(rawPassword.toString()).equals(encodedPassword);
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        log.info("AUTH PROVIDER: " + authProvider);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}