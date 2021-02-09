package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.entities.DiscordServer;
import dev.vrba.studentskyportal.backend.exceptions.discordservers.DiscordServerNotFoundException;
import dev.vrba.studentskyportal.backend.repositories.DiscordServersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class DiscordServersController {

    private final DiscordServersRepository repository;

    @Autowired
    public DiscordServersController(DiscordServersRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/discord-servers")
    public Iterable<DiscordServer> index() {
        return repository.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/discord-servers")
    public void create(@Valid @RequestBody DiscordServer server) {
        repository.save(server);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/admin/discord-servers/{id}")
    public DiscordServer update(@PathVariable long id, @Valid @RequestBody DiscordServer server) {
        return repository.findById(id)
                .map(entry -> {
                    entry.setGuildId(server.getGuildId());
                    entry.setInvite(server.getInvite());

                    return repository.save(entry);
                })
                .orElseThrow(DiscordServerNotFoundException::new);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/admin/discord-servers/{id}")
    public void delete(@PathVariable long id) {
        repository.findById(id).ifPresentOrElse(
                repository::delete,
                () -> { throw new DiscordServerNotFoundException(); }
        );
    }
}
