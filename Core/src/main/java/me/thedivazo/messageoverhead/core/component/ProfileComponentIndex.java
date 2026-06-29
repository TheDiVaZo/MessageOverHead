package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.profile.ProfileId;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@MainThread
public final class ProfileComponentIndex {
    private final Map<UUID, ProfileComponent> byBubbleId = new LinkedHashMap<>();
    private final Map<ProfileId, Set<ProfileComponent>> byProfileId = new LinkedHashMap<>();

    public void add(ProfileComponent component) {
        Objects.requireNonNull(component, "component");

        ProfileComponent previous = byBubbleId.put(component.activeBubble().id(), component);
        if (previous != null && previous != component) {
            removeFromProfile(previous);
        }

        byProfileId
                .computeIfAbsent(component.profileId(), ignored -> new LinkedHashSet<>())
                .add(component);
    }

    public void remove(ProfileComponent component) {
        Objects.requireNonNull(component, "component");

        byBubbleId.remove(component.activeBubble().id(), component);
        removeFromProfile(component);
    }

    public Collection<ProfileComponent> remove(ProfileId profileId) {
        Objects.requireNonNull(profileId, "profileId");

        Set<ProfileComponent> removed = byProfileId.remove(profileId);
        if (removed == null || removed.isEmpty()) {
            return Collections.emptyList();
        }

        for (ProfileComponent component : removed) {
            byBubbleId.remove(component.activeBubble().id(), component);
        }

        return Collections.unmodifiableList(new ArrayList<>(removed));
    }

    public @Nullable ProfileComponent get(UUID activeBubbleId) {
        Objects.requireNonNull(activeBubbleId, "activeBubbleId");

        return byBubbleId.get(activeBubbleId);
    }

    public Collection<ProfileComponent> get(ProfileId profileId) {
        Objects.requireNonNull(profileId, "profileId");

        Set<ProfileComponent> components = byProfileId.get(profileId);
        if (components == null || components.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(new ArrayList<>(components));
    }

    public void clear() {
        byBubbleId.clear();
        byProfileId.clear();
    }

    private void removeFromProfile(ProfileComponent component) {
        Set<ProfileComponent> components = byProfileId.get(component.profileId());
        if (components == null) {
            return;
        }

        components.remove(component);
        if (components.isEmpty()) {
            byProfileId.remove(component.profileId());
        }
    }
}
