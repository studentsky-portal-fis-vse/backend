package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.requests.authentication.LoginRequest;
import dev.vrba.studentskyportal.backend.requests.authentication.RegistrationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public void login(@Valid @RequestBody LoginRequest request) {
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void registration(@Valid @RequestBody RegistrationRequest request) {

    }
}
