package dev.vrba.studentskyportal.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.vrba.studentskyportal.backend.entities.User;
import dev.vrba.studentskyportal.backend.exceptions.authentication.UsernameAlreadyRegisteredException;
import dev.vrba.studentskyportal.backend.exceptions.authentication.UsernameBlacklistedException;
import dev.vrba.studentskyportal.backend.repositories.UsersRepository;
import dev.vrba.studentskyportal.backend.security.UsernameEncoder;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

        assertTrue(result.getResolvedException() instanceof UsernameBlacklistedException);
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

        assertTrue(result.getResolvedException() instanceof UsernameAlreadyRegisteredException);
    }

    @Test
    public void usersCannotLoginWithoutUsername() throws Exception {
        mvc.perform(
                post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "password", "missingUsernameLmao"
                        )))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void usersCannotLoginWithoutPassword() throws Exception {
        mvc.perform(
                post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "username", "no_password_lol"
                        )))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void usersCannotLoginWithInvalidCredentials() throws Exception {
        MvcResult result = mvc.perform(
                post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "username", "vrbj04",
                                "password", "thisOneDoesntWork:("
                        )))
        )
                .andExpect(status().isForbidden())
                .andReturn();

        assertNotNull(result.getResolvedException());
        assertEquals(result.getResolvedException().getMessage(), "Bad credentials");

        usersRepository.save(
                new User(
                        0,
                        "Not me",
                        usernameEncoder.encode("vrbj04"),
                        passwordEncoder.encode("s3cr3tP4assw0rd"),
                        true, // User needs to be verified
                        false,
                        false
                )
        );

        MvcResult result2 = mvc.perform(
                post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "username", "vrbj04",
                                "password", "thisOneDoesntWork:("
                        )))
        )
                .andExpect(status().isForbidden())
                .andReturn();

        assertNotNull(result2.getResolvedException());
        assertEquals("Bad credentials", result2.getResolvedException().getMessage());
    }

    @Test
    public void usersCannotLoginToNonVerifiedAccount() throws Exception {
        usersRepository.save(
                new User(
                        0,
                        "Not me",
                        usernameEncoder.encode("vrbj04"),
                        passwordEncoder.encode("s3cr3tP4assw0rd"),
                        false,
                        false,
                        false
                )
        );

        MvcResult result = mvc.perform(
                post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "username", "vrbj04",
                                "password", "s3cre3tP4assw0rd"
                        )))
        )
                .andExpect(status().isForbidden())
                .andReturn();

        assertNotNull(result.getResolvedException());
        assertEquals("Account with username vrbj04 was not activated yet.", result.getResolvedException().getMessage());
    }

    @Test
    public void usersCannotLoginToBannedAccount() throws Exception {
        usersRepository.save(
                new User(
                        0,
                        "Not me",
                        usernameEncoder.encode("vrbj04"),
                        passwordEncoder.encode("s3cr3tP4assw0rd"),
                        true,
                        true,
                        false
                )
        );

        MvcResult result = mvc.perform(
                post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "username", "vrbj04",
                                "password", "s3cre3tP4assw0rd"
                        )))
        )
                .andExpect(status().isForbidden())
                .andReturn();

        assertNotNull(result.getResolvedException());
        assertEquals("Account with username vrbj04 was banned by site administrators.", result.getResolvedException().getMessage());
    }

    @Test
    public void usersCanLoginToVerifiedAccount() throws Exception {
        usersRepository.save(
                new User(
                        0,
                        "Not me",
                        usernameEncoder.encode("vrbj04"),
                        passwordEncoder.encode("s3cr3tP4assw0rd"),
                        true,
                        false,
                        false
                )
        );

        mvc.perform(get("/api/some-authenticated-endpoint")).andExpect(status().isForbidden());

        mvc.perform(get("/api/some-authenticated-endpoint")
                .header("Authorization", "Bearer thisIsNotAValidToken")
        ) .andExpect(status().isForbidden());

        final MvcResult result = mvc.perform(
                post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(Map.of(
                                "username", "vrbj04",
                                "password", "s3cr3tP4assw0rd"
                        )))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").isNotEmpty())
                .andExpect(jsonPath("token").isString())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectNode node = new ObjectMapper().readValue(content, ObjectNode.class);

        String token = node.get("token").asText();

        mvc.perform(get("/api/some-authenticated-endpoint")
                .header("Authorization", "Bearer " + token)
        )
                // Therefore authentication was successful
                .andExpect(status().isNotFound());
    }
}