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

    // Default token expiration is set to be 8 hours
    private static final long TOKEN_EXPIRATION = 8 * 60 * 60 * 1000;

    private final String secret;

    public JwtTokenService(@Value("${security.jwt.secret}") String secret) {
        this.secret = secret;
    }

    public @NotNull String generateToken(@NotNull Authentication authentication) {
        final User user = (User) authentication.getPrincipal();

        return JWT.create()
                .withSubject(user.getUsername())
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
