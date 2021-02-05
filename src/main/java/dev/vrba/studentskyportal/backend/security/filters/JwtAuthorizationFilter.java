package dev.vrba.studentskyportal.backend.security.filters;

import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.repositories.UsersRepository;
import dev.vrba.studentskyportal.backend.security.JwtTokenService;
import dev.vrba.studentskyportal.backend.security.UserDetailsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtTokenService jwtTokenService;

    private final UserDetailsService userDetailsService;

    private final UsersRepository usersRepository;

    public JwtAuthorizationFilter(
            @NotNull AuthenticationManager authenticationManager,
            @NotNull JwtTokenService jwtTokenService,
            @NotNull UserDetailsService userDetailsService,
            @NotNull UsersRepository usersRepository
    ) {
        super(authenticationManager);

        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(JwtTokenService.TOKEN_PREFIX)) {
            String token = header.replaceFirst(JwtTokenService.TOKEN_PREFIX, "");
            Authentication authentication = getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private @Nullable Authentication getAuthentication(@NotNull String token) {
        try {
            String username = jwtTokenService.verifiedTokenUsername(token);

            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Optional<User> user = usersRepository.findByUsername(username);

                if (user.isPresent()) {
                    return new UsernamePasswordAuthenticationToken(
                            user.get(),
                            userDetails,
                            userDetails.getAuthorities()
                    );
                }
            }
        }
        catch (Exception exception) {
            return null;
        }

        return null;
    }
}
