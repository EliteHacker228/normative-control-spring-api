package ru.maeasoftoworks.normativecontrol.api.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.JwtToken;
import ru.maeasoftoworks.normativecontrol.api.entities.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtils {

    private final String ACCESS_TOKEN_KEY;
    private final long ACCESS_TOKEN_LIFETIME;
    private final String REFRESH_TOKEN_KEY;
    private final long REFRESH_TOKEN_LIFETIME;
    private final String NORMATIVE_CONTROL_API_DOMAIN;

    private JwtUtils(@Value("${jwt.accessToken.key}") String ACCESS_TOKEN_KEY,
                     @Value("${jwt.accessToken.lifetimeInSeconds}") long ACCESS_TOKEN_LIFETIME,
                     @Value("${jwt.refreshToken.key}") String REFRESH_TOKEN_KEY,
                     @Value("${jwt.refreshToken.lifetimeInSeconds}") long REFRESH_TOKEN_LIFETIME,
                     @Value("${normativeControl.api.domain}") String NORMATIVE_CONTROL_API_DOMAIN) {
        this.ACCESS_TOKEN_KEY = ACCESS_TOKEN_KEY;
        this.ACCESS_TOKEN_LIFETIME = ACCESS_TOKEN_LIFETIME;
        this.REFRESH_TOKEN_KEY = REFRESH_TOKEN_KEY;
        this.REFRESH_TOKEN_LIFETIME = REFRESH_TOKEN_LIFETIME;
        this.NORMATIVE_CONTROL_API_DOMAIN = NORMATIVE_CONTROL_API_DOMAIN;
    }

    public JwtToken generateAccessTokenForUser(User user) {
        return generateTokenStringForUser(user, ACCESS_TOKEN_KEY, ACCESS_TOKEN_LIFETIME);
    }

    public JwtToken generateRefreshTokenForUser(User user) {
        return generateTokenStringForUser(user, REFRESH_TOKEN_KEY, REFRESH_TOKEN_LIFETIME);
    }

    private JwtToken generateTokenStringForUser(User user, String key, long tokenLifetime) {
        LocalDateTime tokenIssuingDate = LocalDateTime.now();
        Date tokenExpirationDate = DateUtils.localDateTimeToDate(tokenIssuingDate.plus(tokenLifetime, ChronoUnit.SECONDS));
        Date tokenEntDate = DateUtils.localDateTimeToDate(tokenIssuingDate.plusYears(1L));

        SecretKey signatureKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        JwtBuilder jwt = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claim("aud", NORMATIVE_CONTROL_API_DOMAIN)
                .issuer(NORMATIVE_CONTROL_API_DOMAIN)
                .issuedAt(DateUtils.localDateTimeToDate(tokenIssuingDate))
                .subject(user.getLogin())
                .claim("role", user.getRoles())
                .claim("ent", tokenEntDate)
                .expiration(tokenExpirationDate)
                .signWith(signatureKey, Jwts.SIG.HS256);

        return JwtToken.getJwtTokenFromString(jwt.compact(), key, user);
    }

    public boolean isAccessTokenValid(String jwt){
        return isTokenValid(jwt, ACCESS_TOKEN_KEY);
    }

    public boolean isRefreshTokenValid(String jwt){
        return isTokenValid(jwt, REFRESH_TOKEN_KEY);
    }

    private boolean isTokenValid(String jwt, String key){
        return isTokenReadable(jwt, key) && !isTokenExpired(jwt, key);
    }

    public boolean isAccessTokenReadable(String jwt) {
        return isTokenReadable(jwt, ACCESS_TOKEN_KEY);
    }

    public boolean isRefreshTokenReadable(String jwt) {
        return isTokenReadable(jwt, REFRESH_TOKEN_KEY);
    }

    private boolean isTokenReadable(String jwt, String key) {
        var signatureKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parser().verifyWith(signatureKey).build().parseSignedClaims(jwt);
            return true;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenExpired(String jwt) {
        return isTokenExpired(jwt, REFRESH_TOKEN_KEY);
    }

    public boolean isAccessTokenExpired(String jwt) {
        return isTokenExpired(jwt, ACCESS_TOKEN_KEY);
    }

    private boolean isTokenExpired(String jwt, String key) {
        var signatureKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parser().verifyWith(signatureKey).build().parseSignedClaims(jwt);
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw e;
        }

        return false;
    }
}
