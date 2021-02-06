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
    private String embed;

    @Column(unique = true)
    private String invite;

    public DiscordServer(@NotNull String embed, @NotNull String invite) {
        this.embed = embed;
        this.invite = invite;
    }
}
