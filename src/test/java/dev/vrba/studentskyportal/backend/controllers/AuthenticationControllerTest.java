package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.exceptions.authentication.UsernameAlreadyRegisteredException;
import dev.vrba.studentskyportal.backend.exceptions.authentication.UsernameBlacklistedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Sql(statements = "delete from users")
    public void usersCanRegisterWithoutName() throws Exception {
        mvc.perform(
                post("/api/authentication/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "username", "vrbj04",
                                "password", "lmaolmao"
                        )))
        )
                .andExpect(status().isCreated());
    }

    @Test
    @Sql(statements = "delete from users")
    public void usersCanRegisterWithName() throws Exception {
        mvc.perform(
                post("/api/authentication/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "Jiří Vrba",
                                "username", "vrbj04",
                                "password", "lmaolmao"
                        )))
        )
                .andExpect(status().isCreated());
    }

    @Test
    @Sql(statements = "delete from users")
    public void usersCannotRegisterWithoutUsername() throws Exception {
        mvc.perform(
                post("/api/authentication/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "Hey look, the username is missing",
                                "password", "lmaolmao"
                        )))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(statements = "delete from users")
    public void usersCannotRegisterWithInvalidUsername() throws Exception {
        mvc.perform(
                post("/api/authentication/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "Invalid username",
                                "username", "this.is.not.valid",
                                "password", "lmaolmao"
                        )))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(statements = "delete from users")
    public void usersCannotRegisterWithBlacklistedUsername() throws Exception {
        MvcResult result = mvc.perform(
                post("/api/authentication/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectToJson(Map.of(
                            "username", "adaa04", // Make sure this username is in blacklist.txt
                            "password", "validpassword"
                    )))
        )
                .andExpect(status().isForbidden())
                .andReturn();

        Assertions.assertTrue(result.getResolvedException() instanceof UsernameBlacklistedException);
    }

    @Test
    @Sql(statements = "delete from users")
    public void usersCannotRegisterWithInvalidPassword() throws Exception {
        mvc.perform(
                post("/api/authentication/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "Yo this man has some gap",
                                "username", "b00mer",
                                "password", "xd"
                        )))
        )
                .andExpect(status().isBadRequest());

        mvc.perform(
                post("/api/authentication/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "Probably a bcrypt DOS attempt",
                                "username", "h4ck3r",
                                "password", "xddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"
                        )))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(statements = "insert into users (id, `name`, username, password, is_verified, is_banned, is_admin) values " +
                    "(1, 'Verified user', '1C34F88707B55E6104C4EB20E71FFA3D33E414B71EF689A15FAD0640D0AC58CB'," +
                    "'$2y$12$yx97KW5j8f66U1Lau/YFN.Bx3ADBk8UTFwNKUYYXqpkrtJ7SESH2S', 1, 0, 0)")
    // username = verified, password = verified
    public void usersCannotRegisterUsernameMoreThanOnce() throws Exception {
        MvcResult result = mvc.perform(
                post("/api/authentication/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "name", "Another one",
                                "username", "verified",
                                "password", "thisdoesntmatter"
                        )))
        )
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        Assertions.assertTrue(result.getResolvedException() instanceof UsernameAlreadyRegisteredException);
    }
}