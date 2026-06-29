package me.thedivazo.messageoverhead.profile;

import java.util.Objects;
import java.util.regex.Pattern;

public record ProfileId(String namespace, String value) {
    public static final String DEFAULT_NAMESPACE = "messageoverhead";

    private static final Pattern VALID_PART = Pattern.compile("[a-z0-9_.-]+");

    public ProfileId {
        Objects.requireNonNull(namespace, "namespace");
        Objects.requireNonNull(value, "value");

        validatePart(namespace, "namespace");
        validatePart(value, "value");
    }

    public static ProfileId of(String namespace, String value) {
        return new ProfileId(namespace, value);
    }

    @Override
    public String toString() {
        return namespace + ":" + value;
    }

    private static void validatePart(String value, String partName) {
        if (!VALID_PART.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid profile id " + partName + ": " + value);
        }
    }
}
