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
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.accessToken.key}")
    private String accessTokenKey;
    @Value("${jwt.accessToken.lifetimeInSeconds}")
    private long accessTokenLifetime;
    @Value("${jwt.refreshToken.key}")
    private String refreshTokenKey;
    @Value("${jwt.refreshToken.lifetimeInSeconds}")
    private long refreshTokenLifetime;
    @Value("${normativeControl.api.domain}")
    private String normativeControlApiDomain;

    public JwtToken generateAccessTokenForUser(User user) {
        return generateTokenStringForUser(user, accessTokenKey, accessTokenLifetime);
    }

    public JwtToken generateRefreshTokenForUser(User user) {
        return generateTokenStringForUser(user, refreshTokenKey, refreshTokenLifetime);
    }

    private JwtToken generateTokenStringForUser(User user, String key, long tokenLifetime) {
        LocalDateTime tokenIssuingDate = LocalDateTime.now();
        Date tokenExpirationDate = DateUtils.localDateTimeToDate(tokenIssuingDate.plusSeconds(tokenLifetime));
        Date tokenEntDate = DateUtils.localDateTimeToDate(tokenIssuingDate.plusYears(1L));

        SecretKey signatureKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        JwtBuilder jwt = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claim("aud", normativeControlApiDomain)
                .issuer(normativeControlApiDomain)
                .issuedAt(DateUtils.localDateTimeToDate(tokenIssuingDate))
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .claim("ent", tokenEntDate)
                .expiration(tokenExpirationDate)
                .signWith(signatureKey, Jwts.SIG.HS256);

        return JwtToken.getJwtTokenFromString(jwt.compact(), key, user);
    }

    public Jws<Claims> getClaimsFromAccessTokenString(String jwt){
        return getClaimsFromTokenString(jwt, accessTokenKey);
    }
    public Jws<Claims> getClaimsFromRefreshTokenString(String jwt){
        return getClaimsFromTokenString(jwt, refreshTokenKey);
    }
    private Jws<Claims> getClaimsFromTokenString(String jwt, String key){
        var signatureKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(signatureKey).build().parseSignedClaims(jwt);
    }

    public boolean isAccessTokenValid(String jwt){
        return isTokenValid(jwt, accessTokenKey);
    }

    public boolean isRefreshTokenValid(String jwt){
        return isTokenValid(jwt, refreshTokenKey);
    }

    private boolean isTokenValid(String jwt, String key){
        return isTokenReadable(jwt, key) && !isTokenExpired(jwt, key);
    }

    public boolean isAccessTokenReadable(String jwt) {
        return isTokenReadable(jwt, accessTokenKey);
    }

    public boolean isRefreshTokenReadable(String jwt) {
        return isTokenReadable(jwt, refreshTokenKey);
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
        return isTokenExpired(jwt, refreshTokenKey);
    }

    public boolean isAccessTokenExpired(String jwt) {
        return isTokenExpired(jwt, accessTokenKey);
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