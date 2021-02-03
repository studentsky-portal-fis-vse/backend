package dev.vrba.studentskyportal.backend.security;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UsernameEncoder {
    private final MessageDigest messageDigest;

    public UsernameEncoder(@NotNull String algorithm) throws NoSuchAlgorithmException {
        this.messageDigest = MessageDigest.getInstance("SHA-256");
    }

    public @NotNull String encode(@NotNull String username) {
        return new HexBinaryAdapter().marshal(this.messageDigest.digest(username.getBytes()));
    }
}
