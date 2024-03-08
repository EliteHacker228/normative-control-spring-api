package ru.maeasoftoworks.normativecontrol.api.domain;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.entities.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtToken {

    @Getter
    private final Jws<Claims> jws;

    @Getter
    private final User user;

    @Getter
    private final String compactToken;

    private JwtToken(Jws<Claims> jws, String compactToken, User user){
        this.jws = jws;
        this.compactToken = compactToken;
        this.user = user;
    }

    public static JwtToken getJwtTokenFromString(String jwtCompactString, String jwtSignKey, User user){
        SecretKey key = Keys.hmacShaKeyFor(jwtSignKey.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> parsedJwt = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwtCompactString);
        return new JwtToken(parsedJwt, jwtCompactString, user);
    }
}
