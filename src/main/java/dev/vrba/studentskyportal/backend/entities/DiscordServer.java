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
@Table(name = "discord_servers")
public class DiscordServer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String guildId;

    @Column(unique = true)
    private String invite;

    public DiscordServer(@NotNull String guildId, @NotNull String invite) {
        this.guildId = guildId;
        this.invite = invite;
    }
}
