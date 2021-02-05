package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.entities.UserVerification;
import dev.vrba.studentskyportal.backend.requests.authentication.LoginRequest;
import dev.vrba.studentskyportal.backend.requests.authentication.RegistrationRequest;
import dev.vrba.studentskyportal.backend.services.UserVerificationService;
import dev.vrba.studentskyportal.backend.services.UsersService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final UsersService usersService;

    private final UserVerificationService verificationService;

    @Autowired
    public AuthenticationController(UsersService usersService, UserVerificationService verificationService) {
        this.usersService = usersService;
        this.verificationService = verificationService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        String token = usersService.loginUserAndObtainToken(request.getUsername(), request.getPassword());

        return Map.of("token", token);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public void registration(@Valid @RequestBody RegistrationRequest request) {
        User user = usersService.registerUser(request.getName(), request.getUsername(), request.getPassword());
        UserVerification verification = verificationService.createVerificationForUser(user);

        verificationService.sendVerificationEmail(request.getUsername() + "@vse.cz", verification);
    }

    @GetMapping("/verification/{code}")
    @ResponseStatus(HttpStatus.OK)
    public void verification(@PathVariable @NotNull String code) {
        verificationService.resolveVerificationByCode(code);
    }
}
