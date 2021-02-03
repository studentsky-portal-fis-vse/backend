package dev.vrba.studentskyportal.backend.requests.authentication;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Validated
public class RegistrationRequest {
    @Nullable
    @Length(max = 32)
    private String name = null;

    @NotNull
    @NotBlank
    @Length(min = 6, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$")
    private String username;

    @NotNull
    @NotBlank
    @Length(min = 8, max = 64)
    private String password;
}
