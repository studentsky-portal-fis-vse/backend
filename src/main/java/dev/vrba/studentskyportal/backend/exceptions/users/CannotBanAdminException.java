package dev.vrba.studentskyportal.backend.exceptions.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class CannotBanAdminException extends RuntimeException {
    public CannotBanAdminException() {
        super("Cannot ban an user that has admin permissions.");
    }
}
