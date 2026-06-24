package me.thedivazo.messageoverhead.api;

import me.thedivazo.messageoverhead.core.component.BubbleComponentFactory;
import me.thedivazo.messageoverhead.core.component.ComponentKey;
import me.thedivazo.messageoverhead.core.component.ComponentRegistry;
import me.thedivazo.messageoverhead.core.component.ProfileComponent;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.BubbleProfileRegistry;
import me.thedivazo.messageoverhead.profile.ProfileId;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class ProfileService {
    private final BubbleProfileRegistry registry = new BubbleProfileRegistry();
    private final ComponentRegistry componentRegistry;

    private ProfileId defaultProfileId;

    public ProfileService(ComponentRegistry componentRegistry) {
        this.componentRegistry = Objects.requireNonNull(componentRegistry, "componentRegistry");
    }

    public synchronized void register(BubbleProfile profile) {
        validate(profile);
        registry.register(profile);
    }

    public synchronized @Nullable BubbleProfile unregister(ProfileId id) {
        Objects.requireNonNull(id, "id");

        BubbleProfile profile = registry.unregister(id);
        if (Objects.equals(defaultProfileId, id)) {
            defaultProfileId = null;
        }
        return profile;
    }

    public synchronized @Nullable BubbleProfile get(ProfileId id) {
        Objects.requireNonNull(id, "id");

        return registry.find(id);
    }

    public synchronized @Nullable BubbleProfile defaultProfile() {
        if (defaultProfileId == null) {
            return null;
        }

        BubbleProfile profile = registry.find(defaultProfileId);
        if (profile == null) {
            defaultProfileId = null;
        }
        return profile;
    }

    public synchronized void setDefaultProfile(ProfileId id) {
        Objects.requireNonNull(id, "id");

        if (!registry.contains(id)) {
            throw new IllegalArgumentException("Unknown profile id: " + id);
        }

        defaultProfileId = id;
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
