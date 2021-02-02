package dev.vrba.studentskyportal.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users") // USER is a reserved keyword in psql
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(nullable = true)
    public String name = null;

    @Column(unique = true)
    public String username;

    public String password;

    public boolean isVerified = false;

    public boolean isBanned = false;

    public boolean isAdmin = false;
}
