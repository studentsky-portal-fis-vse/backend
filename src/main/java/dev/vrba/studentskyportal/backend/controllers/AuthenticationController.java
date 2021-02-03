package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.requests.authentication.LoginRequest;
import dev.vrba.studentskyportal.backend.requests.authentication.RegistrationRequest;
import dev.vrba.studentskyportal.backend.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final UsersService usersService;

    public AuthenticationController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        String token = usersService.loginUserAndObtainToken(request.getUsername(), request.getPassword());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return response;
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void registration(@Valid @RequestBody RegistrationRequest request) {

    }
}
