package backend.model.valueObjects;

import org.springframework.security.crypto.password.PasswordEncoder;

public class Password {

    private final String value;

    private Password(String hashedValue) {
        this.value = hashedValue;
    }

    public static Password create(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("Senha fraca");
        }

        return new Password(encoder.encode(rawPassword));
    }

    public static Password fromHash(String hash) {
        return new Password(hash);
    }

    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.value);
    }

    public String getHash() {
        return value;
    }
}
