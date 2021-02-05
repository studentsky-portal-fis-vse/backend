package dev.vrba.studentskyportal.backend;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public @NotNull TransactionalEmailsApi sendInBlueApiClient(@Value("${SIB_API_KEY}") @NotNull String apiKey) {
        ApiClient client = Configuration.getDefaultApiClient();
        ApiKeyAuth authentication = (ApiKeyAuth) client.getAuthentication("api-key");

        authentication.setApiKey(apiKey);

        return new TransactionalEmailsApi(client);
    }
}
