package me.thedivazo.messageoverhead.core.component;

import java.util.Objects;
import java.util.regex.Pattern;

public record ComponentId(String namespace, String value) {
    private static final Pattern VALID_PART =
            Pattern.compile("[a-z0-9_.-]+");

    public ComponentId {
        validate(namespace, "namespace");
        validate(value, "value");
    }

    public static ComponentId of(String namespace, String value) {
        return new ComponentId(namespace, value);
    }

    private static void validate(String value, String name) {
        Objects.requireNonNull(value, name);

        if (!VALID_PART.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid component " + name + ": " + value
            );
        }
    }

    @Override
    public String toString() {
        return namespace + ":" + value;
    }
}
