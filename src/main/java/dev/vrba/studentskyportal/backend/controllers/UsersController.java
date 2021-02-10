package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.exceptions.users.CannotBanAdminException;
import dev.vrba.studentskyportal.backend.exceptions.users.UserNotFoundException;
import dev.vrba.studentskyportal.backend.repositories.UsersRepository;
import dev.vrba.studentskyportal.backend.requests.users.UpdateUserRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UsersController {

    private final UsersRepository repository;

    private final PasswordEncoder passwordEncoder;

    public UsersController(UsersRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<User> index() {
        return repository.findAll();
    }

    @GetMapping("/api/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public User show(@PathVariable long id) {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @PutMapping("/api/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public User update(@PathVariable long id, @Valid @RequestBody UpdateUserRequest request) {
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPassword() != null) {
            String password = passwordEncoder.encode(request.getPassword());
            user.setPassword(password);
        }

        return repository.save(user);
    }

    @PostMapping("/api/users/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public User ban(@PathVariable long id) {
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isAdmin()) {
            throw new CannotBanAdminException();
        }

        user.setBanned(true);
        return repository.save(user);
    }

    @PostMapping("/api/users/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public User unban(@PathVariable long id) {
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);

        user.setBanned(false);
        return repository.save(user);
    }
}
