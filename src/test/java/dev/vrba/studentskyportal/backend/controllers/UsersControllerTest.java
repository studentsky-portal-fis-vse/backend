package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsersControllerTest extends BaseControllerTest {
    @BeforeEach
    public void wipeUsers() {
        usersRepository.deleteAll();
    }

    @Test
    public void anonymousUsersCannotViewUserListing() throws Exception {
        mvc.perform(get("/api/users")).andExpect(status().isForbidden());
    }

    @Test
    public void anonymousUsersCannotViewUserDetails() throws Exception {
        User user = createUser("Some user", "username", "password");
        mvc.perform(get("/api/users/" + user.getId())).andExpect(status().isForbidden());
    }

    @Test
    public void anonymousUsersCannotBanUser() throws Exception {
        User user = createUser("Some user", "username", "password");
        mvc.perform(post("/api/users/" + user.getId() + "/ban")).andExpect(status().isForbidden());
    }

    @Test
    public void anonymousUsersCannotUnbanUser() throws Exception {
        User user = createUser("Some user", "username", "password", true, false, true);
        mvc.perform(post("/api/users/" + user.getId() + "/unban")).andExpect(status().isForbidden());
    }

    @Test
    public void usersCannotViewUserListing() throws Exception {
        User user = createUser("Mr. normie", "username", "password");
        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/users/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void usersCanViewTheirInfo() throws Exception {
        User user = createUser("Mr. normie", "username", "password");
        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").exists())
                .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    public void usersCannotViewOtherUsersInfo() throws Exception {
        User user = createUser("Mr. normie", "username", "password");
        User another = createUser("Mr. another", "another", "password");

        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/users/" + another.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void usersCannotBanUsers() throws Exception {
        User user = createUser("Mr. normie", "username", "password");
        User another = createUser("Mr. another", "another", "password");

        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/users/" + another.getId() + "/ban")
                        .header("Authorization", "Bearer" + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void usersCannotUnbanUsers() throws Exception {
        User user = createUser("Mr. homie", "username", "password");
        User banned = createUser("Mr. banned nigga", "banned", "b4nn3dN1gg4");

        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/users/" + banned.getId() + "/unban")
                        .header("Authorization", "Bearer" + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void usersCanUpdateTheirName() throws Exception {
        User user = createUser("Myspeled naem", "mrdislexia", "password");
        String token = validJwtTokenFor(user);

        mvc.perform(
                put("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "Misspelled name"
                        ))))
                .andExpect(status().isOk());

        User reloaded = usersRepository.findByUsername(user.getUsername()).orElseThrow();

        assertEquals("Misspelled name", reloaded.getName());
        assertEquals(user.getPassword(), reloaded.getPassword());
    }

    @Test
    public void usersCanUpdateTheirNameAndPassword() throws Exception {
        User user = createUser("Myspeled naem", "mrdislexia", "password");
        String token = validJwtTokenFor(user);

        mvc.perform(
                put("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "Misspelled name",
                                "password", "newP4ssW0rd"
                        ))))
                .andExpect(status().isOk());

        User reloaded = usersRepository.findByUsername(user.getUsername()).orElseThrow();

        assertEquals("Misspelled name", reloaded.getName());
        assertNotEquals(user.getPassword(), reloaded.getPassword());
    }

    @Test
    public void usersCanUpdateTheirPassword() throws Exception {
        User user = createUser("Myspeled naem", "mrdislexia", "password");
        String token = validJwtTokenFor(user);

        mvc.perform(
                put("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "password", "newP4ssW0rd"
                        ))))
                .andExpect(status().isOk());

        User reloaded = usersRepository.findByUsername(user.getUsername()).orElseThrow();

        assertEquals("Myspeled naem", reloaded.getName());
        assertNotEquals(user.getPassword(), reloaded.getPassword());
    }

    @Test
    public void usersCannotUpdateOtherUsersNameOrPassword() throws Exception {
        User user = createUser("user", "username", "password");
        User another = createUser("another", "another", "another");
        String token = validJwtTokenFor(user);

        mvc.perform(
                put("/api/users/" + another.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "password", "newP4ssW0rd"
                        ))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void adminsCanViewUserListing() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void adminsCanViewTheirUserDetails() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void adminsCanViewUserDetailsOfOtherUsers() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        User another = createUser("another", "another", "another");
        String token = validJwtTokenFor(user);

        mvc.perform(
                get("/api/users/" + another.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void adminsCanBanUsers() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        User another = createUser("another", "another", "another");

        String token = validJwtTokenFor(user);

        assertFalse(another.isBanned());

        mvc.perform(
                post("/api/users/" + another.getId() + "/ban")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        User reloaded = usersRepository.findByUsername(another.getUsername()).orElseThrow();

        assertTrue(reloaded.isBanned());
    }

    @Test
    public void adminsCanUnbanUsers() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        User banned = createUser("banned", "banned", "banned", true, false, true);

        String token = validJwtTokenFor(user);

        assertTrue(banned.isBanned());

        mvc.perform(
                post("/api/users/" + banned.getId() + "/unban")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        User reloaded = usersRepository.findByUsername(banned.getUsername()).orElseThrow();

        assertFalse(reloaded.isBanned());
    }

    @Test
    public void adminsCanUpdateOtherUsersPassword() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        User another = createUser("another", "another", "another");
        String token = validJwtTokenFor(user);

        mvc.perform(
                put("/api/users/" + another.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "password", "newP4ssW0rd"
                        ))))
                .andExpect(status().isOk());

        User reloaded = usersRepository.findByUsername(another.getUsername()).orElseThrow();

        assertEquals(another.getName(), reloaded.getName());
        assertNotEquals(another.getPassword(), reloaded.getPassword());
    }

    @Test
    public void adminsCanUpdateOtherUsersName() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        User another = createUser("another", "another", "another");
        String token = validJwtTokenFor(user);

        mvc.perform(
                put("/api/users/" + another.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "anotherName"
                        ))))
                .andExpect(status().isOk());

        User reloaded = usersRepository.findByUsername(another.getUsername()).orElseThrow();

        assertNotEquals(another.getName(), reloaded.getName());
        assertEquals(another.getPassword(), reloaded.getPassword());
    }

    @Test
    public void adminsCanUpdateOtherUsersNameAndPassword() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        User another = createUser("another", "another", "another");
        String token = validJwtTokenFor(user);

        mvc.perform(
                put("/api/users/" + another.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "anotherName",
                                "password", "newpassword"
                        ))))
                .andExpect(status().isOk());

        User reloaded = usersRepository.findByUsername(another.getUsername()).orElseThrow();

        assertNotEquals(another.getName(), reloaded.getName());
        assertNotEquals(another.getPassword(), reloaded.getPassword());
    }

    @Test
    public void adminsCannotBanOtherAdmins() throws Exception {
        User user = createUser("user", "username", "password", true, true, false);
        User another = createUser("another", "another", "password", true, true, false);

        String token = validJwtTokenFor(user);

        assertFalse(another.isBanned());

        mvc.perform(
                post("/api/users/" + another.getId() + "/ban")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnprocessableEntity());

        User reloaded = usersRepository.findByUsername(another.getUsername()).orElseThrow();

        assertFalse(reloaded.isBanned());
    }
}
