package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.entities.DiscordServer;
import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.repositories.DiscordServersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DiscordServersControllerTest extends BaseControllerTest {

    @Autowired
    private DiscordServersRepository repository;

    @BeforeEach
    public void wipeDatabase() {
        repository.deleteAll();
        usersRepository.deleteAll();
    }

    @Test
    public void anonymousUsersCannotViewDiscordServers() throws Exception {
        mvc.perform(get("/api/discord-servers")).andExpect(status().isForbidden());
    }

    @Test
    public void anonymousUsersCannotCreateDiscordServers() throws Exception {
        assertEquals(0L, repository.count());

        mvc.perform(
                post("/api/admin/discord-servers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectToJson(Map.of(
                           "guildId", "https://discord.com/mycoolembed.png",
                            "invite", "https://discord.gg/xddlmao"
                    )))
        )
        .andExpect(status().isForbidden());

        assertEquals(0L, repository.count());
    }

    @Test
    public void anonymousUsersCannotUpdateDiscordServers() throws Exception {
        DiscordServer server = repository.save(
                new DiscordServer(
                        42069,
                        "https://discord.gg/lmaoooo"
                )
        );

        assertEquals(1L, repository.count());

        mvc.perform(
                put("/api/admin/discord-servers/" + server.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "guildId", 100_000_000,
                                "invite", "https://discord.gg/xddlmao"
                        )))
        )
                .andExpect(status().isForbidden());

        assertEquals(1L, repository.count());

        DiscordServer refreshed = repository.findAll().iterator().next();

        assertEquals(42069, refreshed.getGuildId());
        assertEquals("https://discord.gg/lmaoooo", refreshed.getInvite());
    }

    @Test
    public void anonymousUsersCannotDeleteDiscordServers() throws Exception {
        DiscordServer server = repository.save(
                new DiscordServer(
                        42069,
                        "https://discord.gg/lmaoooo"
                )
        );

        assertEquals(1L, repository.count());

        mvc.perform(delete("/api/admin/discord-servers/" + server.getId()))
                .andExpect(status().isForbidden());

        assertEquals(1L, repository.count());
    }

    @Test
    public void usersCanViewDiscordServers() throws Exception {
        User user = createUser(null, "vrbj04", "password");
        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/discord-servers")
                    .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    public void usersCannotCreateDiscordServers() throws Exception {
        User user = createUser(null, "vrbj04", "password");
        String token = validJwtTokenFor(user);

        mvc.perform(
                post("/api/admin/discord-servers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "guildId", "https://discord.com/mycoolembed.png",
                                "invite", "https://discord.gg/xddlmao"
                        )))
        )
                .andExpect(status().isForbidden());

        assertEquals(0L, repository.count());
    }

    @Test
    public void usersCannotUpdateDiscordServers() throws Exception {
        User user = createUser(null, "vrbj04", "password");
        String token = validJwtTokenFor(user);

        DiscordServer server = repository.save(
                new DiscordServer(
                        42069,
                        "https://discord.gg/lmaoooo"
                )
        );

        assertEquals(1L, repository.count());

        mvc.perform(
                put("/api/admin/discord-servers/" + server.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "guildId", 420_420_420,
                                "invite", "https://discord.gg/xddlmao"
                        )))
        )
                .andExpect(status().isForbidden());

        assertEquals(1L, repository.count());

        DiscordServer refreshed = repository.findAll().iterator().next();

        assertEquals(42069, refreshed.getGuildId());
        assertEquals("https://discord.gg/lmaoooo", refreshed.getInvite());
    }

    @Test
    public void usersCannotDeleteDiscordServers() throws Exception {
        User user = createUser(null, "vrbj04", "password");
        String token = validJwtTokenFor(user);

        DiscordServer server = repository.save(
                new DiscordServer(
                        42069,
                        "https://discord.gg/lmaoooo"
                )
        );

        assertEquals(1L, repository.count());

        mvc.perform(
                delete("/api/admin/discord-servers/" + server.getId())
                    .header("Authorization", "Bearer " + token)
        )
            .andExpect(status().isForbidden());

        assertEquals(1L, repository.count());
    }

    @Test
    public void adminsCanViewDiscordServers() throws Exception {
        User user = createUser(null, "admin", "password", true, true, false);
        String token = validJwtTokenFor(user);

        mvc.perform(get("/api/discord-servers").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    public void adminsCanCreateDiscordServers() throws Exception {
        User user = createUser(null, "admin", "password", true, true, false);
        String token = validJwtTokenFor(user);

        mvc.perform(
                post("/api/admin/discord-servers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "guildId", 42069,
                                "invite", "https://discord.gg/xddlmao"
                        )))
        )
                .andExpect(status().isCreated());

        DiscordServer server = repository.findAll().iterator().next();

        assertEquals(1L, repository.count());
        assertEquals(42069, server.getGuildId());
        assertEquals("https://discord.gg/xddlmao", server.getInvite());
    }

    @Test
    public void adminsCanUpdateDiscordServers() throws Exception {
        User user = createUser(null, "admin", "password", true, true, false);
        String token = validJwtTokenFor(user);

        DiscordServer server = repository.save(
                new DiscordServer(
                        42069,
                        "https://discord.gg/lmaoooo"
                )
        );

        assertEquals(1L, repository.count());

        mvc.perform(
                put("/api/admin/discord-servers/" + server.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "guildId", 420_420_420,
                                "invite", "https://discord.gg/xddlmao"
                        )))
        )
                .andExpect(status().isOk());

        assertEquals(1L, repository.count());

        DiscordServer refreshed = repository.findAll().iterator().next();

        assertEquals(420_420_420, refreshed.getGuildId());
        assertEquals("https://discord.gg/xddlmao", refreshed.getInvite());
    }

    @Test
    public void adminsCanDeleteDiscordServers() throws Exception {
        User user = createUser(null, "admin", "password", true, true, false);
        String token = validJwtTokenFor(user);

        DiscordServer server = repository.save(
                new DiscordServer(
                        42069,
                        "https://discord.gg/lmaoooo"
                )
        );

        assertEquals(1L, repository.count());

        mvc.perform(
                delete("/api/admin/discord-servers/" + server.getId())
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk());

        assertEquals(0L, repository.count());
    }
}