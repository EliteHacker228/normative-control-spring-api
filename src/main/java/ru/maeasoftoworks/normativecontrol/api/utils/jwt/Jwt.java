package ru.maeasoftoworks.normativecontrol.api.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Getter
public class Jwt {

    private final Jws<Claims> jws;
    private final User user;
    private final String compactToken;

    private Jwt(Jws<Claims> jws, String compactToken, User user){
        this.jws = jws;
        this.compactToken = compactToken;
        this.user = user;
    }

    public static Jwt getJwtTokenFromString(String jwtCompactString, String jwtSignKey, User user){
        SecretKey key = Keys.hmacShaKeyFor(jwtSignKey.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> parsedJwt = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwtCompactString);
        return new Jwt(parsedJwt, jwtCompactString, user);
    }
}