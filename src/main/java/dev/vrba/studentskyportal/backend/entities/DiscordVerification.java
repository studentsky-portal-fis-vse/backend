package dev.vrba.studentskyportal.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

@Data
@Entity
@Table(name = "discord_verifications")
@NoArgsConstructor
@AllArgsConstructor
public class DiscordVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Nullable
    @Column(nullable = true)
    private String discordId = null;

    @OneToOne(cascade = CascadeType.DETACH)
    private User user;

    @Column(nullable = false, unique = true)
    private String code;

    public DiscordVerification(@NotNull User user, @NotNull String code, @Nullable String discordId) {
        this.user = user;
        this.code = code;
        this.discordId = discordId;
    }
}
