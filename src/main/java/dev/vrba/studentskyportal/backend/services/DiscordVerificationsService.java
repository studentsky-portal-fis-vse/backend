package dev.vrba.studentskyportal.backend.services;

import dev.vrba.studentskyportal.backend.entities.DiscordVerification;
import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.repositories.DiscordVerificationsRepository;
import net.bytebuddy.utility.RandomString;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiscordVerificationsService {

    private final DiscordVerificationsRepository verificationsRepository;

    public DiscordVerificationsService(DiscordVerificationsRepository verificationsRepository) {
        this.verificationsRepository  = verificationsRepository;
    }

    public @NotNull DiscordVerification generateVerificationForUser(@NotNull User user) {
        Optional<DiscordVerification> verification = verificationsRepository.findByUserId(user.getId());

        return verification.orElseGet(() -> {
            DiscordVerification newVerification = new DiscordVerification(user, RandomString.make(32), null);

            return verificationsRepository.save(newVerification);
        });
    }
}
