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
import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;

@Service
public class UserVerificationService {

    private final UsersRepository usersRepository;

    private final UserVerificationsRepository verificationsRepository;

    private final TransactionalEmailsApi emailsApi;

    @Autowired
    public UserVerificationService(
            UserVerificationsRepository verificationsRepository,
            UsersRepository usersRepository,
            TransactionalEmailsApi emailsApi
    ) {
        this.verificationsRepository = verificationsRepository;
        this.usersRepository = usersRepository;
        this.emailsApi = emailsApi;
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

    public void sendVerificationEmail(@NotNull String email, @NotNull UserVerification verification) {
        SendSmtpEmail payload = new SendSmtpEmail();
        SendSmtpEmailTo recipient = new SendSmtpEmailTo();
        SendSmtpEmailSender sender = new SendSmtpEmailSender();

        recipient.setEmail(email);

        sender.setName("Studentský portál FIS VŠE");
        sender.setEmail("bot@portal.fis-vse.cz");

        // TODO: update link to match client side api/routing
        String link = "https://portal.fis-vse.cz/app#/verification/" + verification.getCode();

        payload.setSender(sender);
        payload.setTo(List.of(recipient));

        // TODO: make this a template or something
        payload.setSubject("Aktivace účtu na studentském portálu FIS VŠE");
        payload.setTextContent("Aktivace účtu na studentském portálu FIS VŠE\n\nPro váš účet byl vygenerován následující aktivační odkaz: " + link);
        payload.setHtmlContent("<h1>Aktivace účtu na studentském portálu FIS VŠE</h1><br>Pro váš účet byl vygenerován následující aktivační odkaz: <br> <a href=\"" + link + "\" target=\"_blank\">" + link + "</a>");

        try {
            emailsApi.sendTransacEmail(payload);
        }
        catch (ApiException exception) {
            // Map exception to a runtime exception which halts the current request processing
            throw new RuntimeException(exception);
        }
    }
}
