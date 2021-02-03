package dev.vrba.studentskyportal.backend.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vrba.studentskyportal.backend.security.JwtTokenService;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenService service;

    public JwtAuthenticationFilter(@NotNull JwtTokenService service) {
        this.service = service;
    }

    @Data
    private static class Credentials {
        private String username;
        private String password;
    }

    private static class InvalidAuthenticationPayloadException extends AuthenticationException {
        public InvalidAuthenticationPayloadException(@NotNull String message) {
            super(message);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Credentials credentials = new ObjectMapper().readValue(request.getInputStream(), Credentials.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword()
                    )
            );
        }
        catch (Throwable exception) {
            throw new InvalidAuthenticationPayloadException("Invalid payload. Cannot map JSON input to credentials.");
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) {
        response.addHeader("Authorization " , "Bearer " + service.generateToken(authentication));
    }
}
