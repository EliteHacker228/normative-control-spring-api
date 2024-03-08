package ru.maeasoftoworks.normativecontrol.api;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.AccessToken;
import ru.maeasoftoworks.normativecontrol.api.entities.Document;
import ru.maeasoftoworks.normativecontrol.api.entities.RefreshToken;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.repositories.AccessTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.DocumentsRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.RefreshTokensRepository;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;
import ru.maeasoftoworks.normativecontrol.api.utils.JwtUtils;

import java.util.List;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class DbTests {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccessTokensRepository accessTokensRepository;

    @Autowired
    private RefreshTokensRepository refreshTokensRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Test
    public void userSavingAndFindingTest() {
        usersRepository.deleteAll();

        usersRepository.save(new User("Kuznetsov.Mikhail@urfu.me", "Кузнецов М.А.", "misha.kuznetsov", "ilovepuppies2548", List.of(Role.STUDENT), "UrFU"));
        usersRepository.save(new User("Mikhalkov.Valeriy@at.urfu.ru", "Михалков В.Л.", "mixalkoW", "1234qwerty5678", List.of(Role.INSPECTOR), "UrFU"));
        usersRepository.save(new User("Karpov.Ilya@urfu.ru", "Карпов И.Н", "Karpov1974", "i<3rtf", List.of(Role.ADMIN), "UrFU"));
        for (User findedStrudent : usersRepository.findAll()) {
            System.out.println(findedStrudent);
        }
    }

    @Test
    public void userFindingAndEditingTest() {
        usersRepository.deleteAll();

        usersRepository.save(new User("Kuznetsov.Mikhail@urfu.me", "Кузнецов М.А.", "misha.kuznetsov", "ilovepuppies2548", List.of(Role.STUDENT), "UrFU"));
        var user = usersRepository.findAll().get(0);
        System.out.println(user);
        user.setEmail("KuznetsovMisha@urfu.ru");
        usersRepository.save(user);
        user = usersRepository.findAll().get(0);
        System.out.println(user);
    }

    @Test
    @Transactional
    public void userRegistrationTest() {
        usersRepository.deleteAll();

        User user = new User("Kuznetsov.Mikhail@urfu.me", "Кузнецов М.А.", "misha.kuznetsov", "ilovepuppies2548", List.of(Role.STUDENT), "UrFU");

        JwtToken jwtAccessToken = jwtUtils.generateAccessTokenForUser(user);
        AccessToken accessToken = new AccessToken(user,
                jwtAccessToken.getCompactToken(),
                jwtAccessToken.getJws().getPayload().getIssuedAt(),
                jwtAccessToken.getJws().getPayload().getExpiration());
        accessToken.setUser(user);

        JwtToken jwtRefreshToken = jwtUtils.generateRefreshTokenForUser(user);
        RefreshToken refreshToken = new RefreshToken(user,
                jwtRefreshToken.getCompactToken(),
                jwtRefreshToken.getJws().getPayload().getIssuedAt(),
                jwtRefreshToken.getJws().getPayload().getExpiration());
        refreshToken.setUser(user);

        usersRepository.save(user);
        accessTokensRepository.save(accessToken);
        refreshTokensRepository.save(refreshToken);

        for(User usr: usersRepository.findAll())
            System.out.println(usr);

        for(AccessToken acsTkn: accessTokensRepository.findAll())
            System.out.println(acsTkn);

        for(RefreshToken rfrshToken: refreshTokensRepository.findAll())
            System.out.println(rfrshToken);

        Document document = new Document(user, System.currentTimeMillis());
        document.setUser(user);
        documentsRepository.save(document);

        document = new Document(user, System.currentTimeMillis() + 2500);
        document.setUser(user);
        documentsRepository.save(document);

        document = new Document(user, System.currentTimeMillis());
        document.setUser(user);
        documentsRepository.save(document);

        for(Document doc: documentsRepository.findAll())
            System.out.println(doc);
    }
}
