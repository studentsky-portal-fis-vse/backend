package dev.vrba.studentskyportal.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Data
@Entity
@Table(name = "discord_servers")
@NoArgsConstructor
@AllArgsConstructor
public class DiscordServer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private long guildId;

    @Column(unique = true)
    private String invite;

    public DiscordServer(long guildId, @NotNull String invite) {
        this.guildId = guildId;
        this.invite = invite;
    }
}
