package dev.vrba.studentskyportal.backend.services;

import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.entities.UserVerification;
import dev.vrba.studentskyportal.backend.repositories.UserVerificationsRepository;
import net.bytebuddy.utility.RandomString;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationService {
    private final UserVerificationsRepository verificationsRepository;

    @Autowired
    public UserVerificationService(UserVerificationsRepository verificationsRepository) {
        this.verificationsRepository = verificationsRepository;
    }

    public @NotNull UserVerification createVerificationForUser(@NotNull User user) {
        UserVerification verification = new UserVerification(user, RandomString.make(64));
        return verificationsRepository.save(verification);
    }
}
