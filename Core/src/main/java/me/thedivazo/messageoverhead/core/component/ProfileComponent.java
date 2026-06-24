package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.profile.ProfileId;

import java.util.Set;

public class ProfileComponent implements BubbleComponent {
    private static final ComponentKey<ProfileComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "profile"),
            ProfileComponent.class,
            ComponentMetadata.EMPTY
    );

    private final ActiveBubble activeBubble;
    private final ProfileId profileId;

    public ProfileComponent(ActiveBubble activeBubble, ProfileId profileId) {
        this.activeBubble = activeBubble;
        this.profileId = profileId;
    }

    public ProfileId getProfileId() {
        return profileId;
    }

    public static ComponentKey<ProfileComponent> key() {
        return KEY;
    }

    public static ProfileComponent attach(ActiveBubble activeBubble, ProfileId profileId) {
        return activeBubble.container().attach(key(), bubble -> new ProfileComponent(bubble.bubble(), profileId));
    }
}
