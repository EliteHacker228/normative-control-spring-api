package ru.maeasoftoworks.normativecontrol.api;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.RefreshToken;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.repositories.RefreshTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.services.AccountService;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.util.List;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
//@SpringBootApplication
@AllArgsConstructor
public class NormativeControlApiApplication {

    private static final Logger log = LoggerFactory.getLogger(NormativeControlApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(NormativeControlApiApplication.class, args);
    }

    private UsersRepository usersRepository;
    private RefreshTokensRepository refreshTokensRepository;
    private JwtUtils jwtUtils;

    @PostConstruct
    @Transactional
    protected void initDatabase() {
        if(!usersRepository.existsByEmail("inspector@urfu.me")) {
            User normocontroller = new User();
            normocontroller.setEmail("inspector@urfu.me");
            normocontroller.setPassword("inspector");
            normocontroller.setFirstName("Михаил");
            normocontroller.setMiddleName("Андреевич");
            normocontroller.setLastName("Кузнецов");
            normocontroller.setAcademicGroup(null);
            normocontroller.setOrganization("UrFU");
            normocontroller.setRole(Role.INSPECTOR);

            JwtToken normocontrollerJwtRefreshToken = jwtUtils.generateRefreshTokenForUser(normocontroller);
            RefreshToken normocontrollerRefreshToken = new RefreshToken(normocontroller,
                    normocontrollerJwtRefreshToken.getCompactToken(),
                    normocontrollerJwtRefreshToken.getJws().getPayload().getIssuedAt(),
                    normocontrollerJwtRefreshToken.getJws().getPayload().getExpiration());
            usersRepository.save(normocontroller);
            refreshTokensRepository.save(normocontrollerRefreshToken);
            log.info("Inspector account created. Login: inspector@urfu.me; Password: inspector;");
        }

        if(!usersRepository.existsByEmail("admin@urfu.me")) {
            User admin = new User();
            admin.setEmail("admin@urfu.me");
            admin.setPassword("admin");
            admin.setFirstName("Виктор");
            admin.setMiddleName("Николаевич");
            admin.setLastName("Салтыков");
            admin.setAcademicGroup(null);
            admin.setOrganization("UrFU");
            admin.setRole(Role.ADMIN);

            JwtToken adminJwtRefreshToken = jwtUtils.generateRefreshTokenForUser(admin);
            RefreshToken adminRefreshToken = new RefreshToken(admin,
                    adminJwtRefreshToken.getCompactToken(),
                    adminJwtRefreshToken.getJws().getPayload().getIssuedAt(),
                    adminJwtRefreshToken.getJws().getPayload().getExpiration());
            usersRepository.save(admin);
            refreshTokensRepository.save(adminRefreshToken);
            log.info("Admin account created. Login: admin@urfu.me; Password: admin;");
        }
    }
}
