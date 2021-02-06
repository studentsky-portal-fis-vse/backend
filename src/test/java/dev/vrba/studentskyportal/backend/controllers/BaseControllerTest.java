package dev.vrba.studentskyportal.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.repositories.UsersRepository;
import dev.vrba.studentskyportal.backend.security.JwtTokenService;
import dev.vrba.studentskyportal.backend.security.UsernameEncoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

public abstract class BaseControllerTest {

    @Autowired
    protected UsersRepository usersRepository;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected UsernameEncoder usernameEncoder;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    protected @NotNull User createUser(@Nullable String name, @NotNull String username, @NotNull String password) {
        return createUser(name, username, password, true, false, false);
    }

    protected @NotNull User createUser(
            @Nullable String name,
            @NotNull String username,
            @NotNull String password,
            boolean isVerified,
            boolean isAdmin,
            boolean isBanned
    ) {
        User user = new User(
                name,
                usernameEncoder.encode(username),
                passwordEncoder.encode(password)
        );

        user.setVerified(isVerified);
        user.setAdmin(isAdmin);
        user.setBanned(isBanned);

        return this.usersRepository.save(user);
    }

    protected @NotNull String validJwtTokenFor(@NotNull User user) {
        return jwtTokenService.generateToken(user);
    }

    protected static @NotNull String objectToJson(final @NotNull Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
