package dev.vrba.studentskyportal.backend.exceptions.authentication;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserVerificationNotFoundException extends RuntimeException {
    public UserVerificationNotFoundException(@NotNull String message) {
        super(message);
    }
}
