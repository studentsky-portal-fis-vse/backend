package dev.vrba.studentskyportal.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public abstract class BaseControllerTest {

    protected static @NotNull String objectToJson(final @NotNull Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
