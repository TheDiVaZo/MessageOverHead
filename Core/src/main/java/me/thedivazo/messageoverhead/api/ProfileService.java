package me.thedivazo.messageoverhead.api;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.component.ProfileComponent;
import me.thedivazo.messageoverhead.core.component.ProfileComponentIndex;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.ProfileId;
import me.thedivazo.messageoverhead.profile.ProfileRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@MainThread
public final class ProfileService {
    private final ProfileRegistry registry;
    private final ComponentRegistry componentRegistry;
    private final ProfileComponentIndex profileIndex;

    public ProfileService(ProfileRegistry registry, ComponentRegistry componentRegistry, ProfileComponentIndex profileIndex) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.componentRegistry = Objects.requireNonNull(componentRegistry, "componentRegistry");
        this.profileIndex = Objects.requireNonNull(profileIndex, "profileIndex");
    }

    public void register(BubbleProfile profile) {
        validate(profile);
        registry.register(profile);
    }

    public @Nullable BubbleProfile unregister(ProfileId id) {
        Objects.requireNonNull(id, "id");

        BubbleProfile profile = registry.unregister(id);
        if (profile == null) {
            return null;
        }

        profileIndex.get(id).forEach(component -> component.activeBubble().remove());
        profileIndex.remove(id);
        return profile;
    }

    public @Nullable BubbleProfile get(ProfileId id) {
        Objects.requireNonNull(id, "id");

        return registry.find(id);
    }

    private void validate(BubbleProfile profile) {
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(profile.id(), "profile.id");
        Objects.requireNonNull(profile.bubbleFactory(), "profile.bubbleFactory");

        Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories =
                Objects.requireNonNull(profile.componentFactories(), "profile.componentFactories");

        for (Map.Entry<ComponentKey<?>, BubbleComponentFactory<?>> entry : componentFactories.entrySet()) {
            ComponentKey<?> key = Objects.requireNonNull(entry.getKey(), "component key");
            Objects.requireNonNull(entry.getValue(), "component factory for " + key.id());

            if (ProfileComponent.key().equals(key)) {
                throw new IllegalArgumentException(
                        "Profile component is managed by SpawnService and cannot be included in profile " + profile.id()
                );
            }

            if (!isRegisteredComponent(key)) {
                throw new IllegalArgumentException("Component key is not registered: " + key.id());
            }
        }
    }

    private boolean isRegisteredComponent(ComponentKey<?> key) {
        try {
            return componentRegistry.isValid(key);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid component key: " + key.id(), exception);
        }
    }
}
