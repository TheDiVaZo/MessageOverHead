package me.thedivazo.messageoverhead.core.component;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.ProfileId;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MainThread
public class ProfileComponent implements BubbleComponent {
    private static final ComponentKey<ProfileComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "profile"),
            ProfileComponent.class,
            ComponentMetadata.EMPTY
    );

    private final ActiveBubble activeBubble;
    private final BubbleProfile profile;
    private final BubbleProfileContainer container;

    public ProfileComponent(ActiveBubble activeBubble, BubbleProfile profile, BubbleProfileContainer container) {
        this.activeBubble = activeBubble;
        this.profile = profile;
        this.container = container;
    }

    @Override
    public void onAttached() {
        container.put(this);
    }

    @Override
    public void onDetached() {
        container.remove(this);
    }

    public BubbleProfile getProfile() {
        return profile;
    }

    public ActiveBubble getActiveBubble() {
        return activeBubble;
    }

    public static ComponentKey<ProfileComponent> key() {
        return KEY;
    }

    public static class Factory {
        private final BubbleProfileContainer container;

        public Factory(BubbleProfileContainer container) {
            this.container = container;
        }

        public ProfileComponent create(ComponentContext context, BubbleProfile profile) throws Exception {
            return new ProfileComponent(context.bubble(), profile, container);
        }
    }

    public static class BubbleProfileContainer {
        private Map<UUID, ProfileComponent> bubbleIdToProfiles = new HashMap<>();
        private Multimap<ProfileId, ProfileComponent> profileIdToProfiles = MultimapBuilder.hashKeys().arrayListValues().build();

        public void put(ProfileComponent profileComponent) {
            bubbleIdToProfiles.put(profileComponent.activeBubble.id(), profileComponent);
            profileIdToProfiles.put(profileComponent.getProfile().id(), profileComponent);
        }

        public void remove(ProfileComponent profileComponent) {
            bubbleIdToProfiles.remove(profileComponent.activeBubble.id());
            profileIdToProfiles.removeAll(profileComponent.getProfile().id());
        }

        public void remove(ProfileId profileId) {
            Collection<ProfileComponent> removedComponents = profileIdToProfiles.removeAll(profileId);
            removedComponents.forEach(component -> bubbleIdToProfiles.remove(component.activeBubble.id()));
        }

        @Nullable
        public ProfileComponent get(UUID activeBubbleId) {
            return bubbleIdToProfiles.get(activeBubbleId);
        }

        public Collection<ProfileComponent> get(ProfileId profileId) {
            return profileIdToProfiles.get(profileId);
        }
    }
}
