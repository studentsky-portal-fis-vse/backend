package dev.vrba.studentskyportal.backend.controllers;

import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.exceptions.authentication.UsernameAlreadyRegisteredException;
import dev.vrba.studentskyportal.backend.exceptions.authentication.UsernameBlacklistedException;
import dev.vrba.studentskyportal.backend.repositories.UsersRepository;
import dev.vrba.studentskyportal.backend.security.UsernameEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
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

    @Autowired
    private UsernameEncoder usernameEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    public void wipeUsersTable() {
        usersRepository.deleteAll();
    }

    @Test
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
    // username = verified, password = verified
    public void usersCannotRegisterUsernameMoreThanOnce() throws Exception {

        usersRepository.save(
                new User(
                    "Already registered",
                    usernameEncoder.encode("verified"),
                    passwordEncoder.encode("secretPassword")
                )
        );

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