package dev.vrba.studentskyportal.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @OneToOne
    private User user;

    @Column(unique = true, nullable = false)
    @NotNull
    private String code;

    public UserVerification(@NotNull User user, @NotNull String code) {
        this.id = 0;
        this.user = user;
        this.code = code;
    }
}
