package me.thedivazo.messageoverhead.profile;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class BubbleProfileRegistry {
    private final Map<ProfileId, BubbleProfile> profiles =
            new LinkedHashMap<>();

    public void register(BubbleProfile profile) {
        Objects.requireNonNull(profile, "profile");

        ProfileId id = Objects.requireNonNull(profile.id(), "profile.id");
        BubbleProfile existing = profiles.get(id);

        if (existing != null && existing != profile) {
            throw new IllegalStateException(
                    "Profile ID " + id + " is already registered"
            );
        }

        profiles.put(id, profile);
    }

    public @Nullable BubbleProfile unregister(ProfileId id) {
        Objects.requireNonNull(id, "id");

        return profiles.remove(id);
    }

    public @Nullable BubbleProfile find(ProfileId id) {
        Objects.requireNonNull(id, "id");

        return profiles.get(id);
    }

    public boolean contains(ProfileId id) {
        Objects.requireNonNull(id, "id");

        return profiles.containsKey(id);
    }

    public Map<ProfileId, BubbleProfile> profiles() {
        return Collections.unmodifiableMap(profiles);
    }

    public void clear() {
        profiles.clear();
    }
}
