package dev.vrba.studentskyportal.backend.services;

import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.entities.UserVerification;
import dev.vrba.studentskyportal.backend.exceptions.authentication.UserVerificationNotFoundException;
import dev.vrba.studentskyportal.backend.repositories.UserVerificationsRepository;
import dev.vrba.studentskyportal.backend.repositories.UsersRepository;
import net.bytebuddy.utility.RandomString;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationService {
    private final UserVerificationsRepository verificationsRepository;

    private final UsersRepository usersRepository;

    @Autowired
    public UserVerificationService(UserVerificationsRepository verificationsRepository, UsersRepository usersRepository) {
        this.verificationsRepository = verificationsRepository;
        this.usersRepository = usersRepository;
    }

    public @NotNull UserVerification createVerificationForUser(@NotNull User user) {
        UserVerification verification = new UserVerification(user, RandomString.make(64));
        return verificationsRepository.save(verification);
    }

    public void resolveVerificationByCode(@NotNull String code) {
        UserVerification verification = verificationsRepository.findByCode(code)
                .orElseThrow(() -> new UserVerificationNotFoundException("Verification with code " + code + " not found."));

        User user = verification.getUser();

        user.setVerified(true);

        usersRepository.save(user);
        verificationsRepository.delete(verification);
    }
}
