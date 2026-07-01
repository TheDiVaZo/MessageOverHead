package me.thedivazo.messageoverhead.core.component;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.ProfileId;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@MainThread
public final class ProfileComponent implements BubbleComponent {
    private static final ComponentKey<ProfileComponent> KEY = new ComponentKey<>(
            ComponentId.of("messageoverhead", "profile"),
            ProfileComponent.class,
            ComponentMetadata.EMPTY
    );

    private final ActiveBubble activeBubble;
    private final BubbleProfile profile;
    private final ProfileComponentIndex index;

    public ProfileComponent(ActiveBubble activeBubble, BubbleProfile profile, ProfileComponentIndex index) {
        this.activeBubble = Objects.requireNonNull(activeBubble, "activeBubble");
        this.profile = Objects.requireNonNull(profile, "profile");
        this.index = Objects.requireNonNull(index, "index");
    }

    @Override
    public void onPreAttached() {
        index.add(this);
    }

    @Override
    public void onDetached() {
        index.remove(this);
    }

    public BubbleProfile profile() {
        return profile;
    }

    public BubbleProfile getProfile() {
        return profile();
    }

    public ProfileId profileId() {
        return profile.id();
    }

    public ProfileId getProfileId() {
        return profileId();
    }

    public ActiveBubble activeBubble() {
        return activeBubble;
    }

    public ActiveBubble getActiveBubble() {
        return activeBubble();
    }

    public static ComponentKey<ProfileComponent> key() {
        return KEY;
    }

    public static Factory factory(ProfileComponentIndex index, BubbleProfile profile) {
        return new Factory(index, profile);
    }

    public static @Nullable ProfileComponent detach(ActiveBubble activeBubble) {
        return activeBubble.container().detach(key());
    }

    public static @Nullable ProfileComponent get(ActiveBubble activeBubble) {
        return activeBubble.container().get(key());
    }

    public static boolean contains(ActiveBubble activeBubble) {
        return activeBubble.container().contains(key());
    }

    public static final class Factory implements BubbleComponentFactory<ProfileComponent> {
        private final ProfileComponentIndex index;
        private final BubbleProfile profile;

        private Factory(ProfileComponentIndex index, BubbleProfile profile) {
            this.index = Objects.requireNonNull(index, "index");
            this.profile = Objects.requireNonNull(profile, "profile");
        }

        @Override
        public ProfileComponent create(ComponentContext context) {
            return new ProfileComponent(context.bubble(), profile, index);
        }
    }
}
