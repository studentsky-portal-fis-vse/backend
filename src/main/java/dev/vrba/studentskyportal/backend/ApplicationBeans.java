package dev.vrba.studentskyportal.backend;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;

import java.util.logging.Logger;

@Component
public class ApplicationBeans {

    @Bean
    @Profile("production")
    public @NotNull TransactionalEmailsApi sendInBlueApiClient(@Value("${mail.api-key}") @NotNull String apiKey) {

        ApiClient client = Configuration.getDefaultApiClient();
        ApiKeyAuth authentication = (ApiKeyAuth) client.getAuthentication("api-key");

        authentication.setApiKey(apiKey);

        return new TransactionalEmailsApi(client);
    }

    @Bean
    @Profile("!production")
    public @NotNull TransactionalEmailsApi fakeApiClient() {

        class FakeTransactionalEmailsApi extends TransactionalEmailsApi {
            @Override
            public CreateSmtpEmail sendTransacEmail(SendSmtpEmail sendSmtpEmail) throws ApiException {
                // Do no actually call the external service...
                Logger.getLogger(this.getClass().getName()).info("Sending email:\n" + sendSmtpEmail.toString());
                return new CreateSmtpEmail();
            }
        }

        return new FakeTransactionalEmailsApi();
    }
}
