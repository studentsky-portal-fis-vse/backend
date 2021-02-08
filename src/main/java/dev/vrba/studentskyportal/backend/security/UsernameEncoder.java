package dev.vrba.studentskyportal.backend.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class UsernameEncoder {
    private final MessageDigest messageDigest;

    public UsernameEncoder() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        this.messageDigest = MessageDigest.getInstance("RipeMD160", "BC");
    }

    public @NotNull String encode(@NotNull String username) {
        return new HexBinaryAdapter().marshal(this.messageDigest.digest(username.getBytes()));
    }
}
