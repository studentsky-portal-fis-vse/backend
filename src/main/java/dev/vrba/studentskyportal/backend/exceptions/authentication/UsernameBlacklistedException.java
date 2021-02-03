package dev.vrba.studentskyportal.backend.exceptions.authentication;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsernameBlacklistedException extends RuntimeException {
    public UsernameBlacklistedException(@NotNull String username) {
        super("Username " + username + " is blacklisted.");
    }
}
