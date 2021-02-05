package dev.vrba.studentskyportal.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_verifications")
public class UserVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @OneToOne(cascade = CascadeType.DETACH)
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
