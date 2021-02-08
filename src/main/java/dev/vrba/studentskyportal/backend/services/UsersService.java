package dev.vrba.studentskyportal.backend.services;

import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.exceptions.authentication.LoginException;
import dev.vrba.studentskyportal.backend.exceptions.authentication.UsernameAlreadyRegisteredException;
import dev.vrba.studentskyportal.backend.exceptions.authentication.UsernameBlacklistedException;
import dev.vrba.studentskyportal.backend.repositories.UsersRepository;
import dev.vrba.studentskyportal.backend.security.JwtTokenService;
import dev.vrba.studentskyportal.backend.security.UsernameEncoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class UsersService {
    private final UsersRepository repository;
    private final UsernameEncoder usernameEncoder;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final List<String> blacklist;

    public UsersService(
            @NotNull UsersRepository repository,
            @NotNull UsernameEncoder usernameEncoder,
            @NotNull PasswordEncoder passwordEncoder,
            @NotNull JwtTokenService jwtTokenService,
            @NotNull AuthenticationManager authenticationManager,
            @NotNull ResourceLoader loader
    ) {
        this.repository = repository;
        this.usernameEncoder = usernameEncoder;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;

        InputStream blacklist = getClass().getResourceAsStream("/blacklist.txt");
        this.blacklist = new Scanner(blacklist)
                .useDelimiter("\n")
                .tokens()
                .collect(Collectors.toList());
    }

    public @NotNull User registerUser(@Nullable String name, @NotNull String username, @NotNull String password) {
        User user = new User(
            name,
            usernameEncoder.encode(username),
            passwordEncoder.encode(password)
        );

        // Lookup needs to be performed with an encoded username
        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyRegisteredException(username);
        }

        if (blacklist.contains(username)) {
            throw new UsernameBlacklistedException(username);
        }

        return repository.save(user);
    }

    public @NotNull String loginUserAndObtainToken(@NotNull String username, @NotNull String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                usernameEncoder.encode(username),
                password
        );
        SecurityContext context = SecurityContextHolder.getContext();

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            context.setAuthentication(authentication);
        }
        catch (BadCredentialsException exception) {
            throw new LoginException("Bad credentials");
        }
        catch (LockedException exception) {
            throw new LoginException("Account with username " + username + " was not activated yet.");
        }
        catch (DisabledException exception) {
            throw new LoginException("Account with username " + username + " was banned by site administrators.");
        }

        return jwtTokenService.generateToken(context.getAuthentication());
    }
}
