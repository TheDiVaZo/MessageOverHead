package me.thedivazo.messageoverhead.api;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.component.ProfileComponent;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.ProfileRegistry;
import me.thedivazo.messageoverhead.profile.ProfileId;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@MainThread
public final class ProfileService {
    private final ProfileRegistry registry;
    private final ComponentRegistry componentRegistry;
    private final ProfileComponent.BubbleProfileContainer profileContainer;

    public ProfileService(ProfileRegistry registry, ComponentRegistry componentRegistry, ProfileComponent.BubbleProfileContainer profileContainer) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.componentRegistry = Objects.requireNonNull(componentRegistry, "componentRegistry");
        this.profileContainer = Objects.requireNonNull(profileContainer, "profileContainer");
    }

    public synchronized void register(BubbleProfile profile) {
        validate(profile);
        registry.register(profile);
    }

    public synchronized @Nullable BubbleProfile unregister(ProfileId id) {
        Objects.requireNonNull(id, "id");

        BubbleProfile profile = registry.unregister(id);

        profileContainer.get(id).forEach(component -> component.getActiveBubble().remove());
        profileContainer.remove(id);
        return profile;
    }

    public synchronized @Nullable BubbleProfile get(ProfileId id) {
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
