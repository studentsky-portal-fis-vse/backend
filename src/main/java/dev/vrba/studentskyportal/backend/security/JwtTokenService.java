package dev.vrba.studentskyportal.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.vrba.studentskyportal.backend.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {

    public static final String TOKEN_PREFIX = "Bearer ";

    // Default token expiration is set to be 15 minutes
    public static final long TOKEN_EXPIRATION = 15 * 60 * 1000;

    private final String secret;

    public JwtTokenService(@Value("${security.jwt.secret}") String secret) {
        this.secret = secret;
    }

    public @NotNull String generateToken(@NotNull Authentication authentication) {
        return generateToken(authentication.getName());
    }

    public @NotNull String generateToken(@NotNull User user) {
        return generateToken(user.getUsername());
    }

    public @NotNull String generateToken(@NotNull String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(secret));
    }

    public @Nullable String verifiedTokenUsername(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
           .build()
           .verify(token)
           .getSubject();
    }
}
