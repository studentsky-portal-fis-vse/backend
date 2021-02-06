package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.entities.DiscordVerification;
import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.services.DiscordVerificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discord-verifications")
public class DiscordVerificationsController {

    private final DiscordVerificationsService service;

    @Autowired
    public DiscordVerificationsController(DiscordVerificationsService service) {
        this.service = service;
    }


    @GetMapping("/verification")
    public DiscordVerification verification(Authentication authentication) {
        return service.generateVerificationForUser((User) authentication.getPrincipal());
    }
}
