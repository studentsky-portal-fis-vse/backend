package dev.vrba.studentskyportal.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // USER is a reserved keyword in psql
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    @Column(nullable = true)
    private String name = null;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private boolean isVerified = false;

    private boolean isBanned = false;

    private boolean isAdmin = false;

    public User(@Nullable String name, @NotNull String username, @NotNull String password) {
        // This will be overridden by Hibernate internals
        this.id = 0;
        this.name = name;
        this.username = username;
        this.password = password;
    }
}
