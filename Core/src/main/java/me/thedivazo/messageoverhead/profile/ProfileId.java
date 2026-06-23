package me.thedivazo.messageoverhead.profile;

import java.util.Objects;
import java.util.regex.Pattern;

public record ProfileId(String value) {
    private static final Pattern VALID_VALUE =
            Pattern.compile("[a-z0-9_.-]+(?::[a-z0-9_.-]+)?");

    public ProfileId {
        Objects.requireNonNull(value, "value");

        if (!VALID_VALUE.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid profile id: " + value);
        }
    }

    public static ProfileId of(String value) {
        return new ProfileId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
