package dev.vrba.studentskyportal.backend.exceptions.authentication;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class VerificationEmailDeliveryException extends RuntimeException {
    public VerificationEmailDeliveryException(@NotNull String message) {
        super(message);
    }
}
