package dev.vrba.studentskyportal.backend.requests.users;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.Nullable;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UpdateUserRequest {
    @Nullable
    @Length(max = 32)
    private String name;

    @Nullable
    @Length(min = 8, max = 64)
    private String password;
}
