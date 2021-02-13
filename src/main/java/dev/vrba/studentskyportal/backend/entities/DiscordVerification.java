package dev.vrba.studentskyportal.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "discord_verifications")
public class DiscordVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Nullable
    @Column(nullable = true)
    @JsonIgnore
    private String discordId = null;

    @Column(nullable = false, unique = true)
    private String code;

    public DiscordVerification(@NotNull User user, @NotNull String code, @Nullable String discordId) {
        this.user = user;
        this.code = code;
        this.discordId = discordId;
    }
}
