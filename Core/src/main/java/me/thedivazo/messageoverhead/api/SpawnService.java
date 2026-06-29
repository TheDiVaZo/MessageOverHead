package me.thedivazo.messageoverhead.api;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Author;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.component.ProfileComponent;
import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.ProfileId;
import me.thedivazo.messageoverhead.profile.ProfileRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@MainThread
public final class SpawnService {
    private final BubbleScheduler scheduler;
    private final ProfileComponent.Factory profileFactory;
    private final ProfileComponent.BubbleProfileContainer profileContainer;
    private final ProfileRegistry registry;

    public SpawnService(
            BubbleScheduler scheduler, ProfileComponent.BubbleProfileContainer profileContainer, ProfileRegistry registry
    ) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.profileFactory = new ProfileComponent.Factory(profileContainer);
        this.profileContainer = Objects.requireNonNull(profileContainer, "profileContainer");
        this.registry = registry;
    }

    public ActiveBubble spawn(Message message, Author author, ProfileId profileId) {
        return spawn(message, author, Objects.requireNonNull(registry.find(profileId)));
    }

    public ActiveBubble spawn(Message message, Author author, BubbleProfile profile) {
        Objects.requireNonNull(profile, "profile");

        SchedulableBubble schedulable = profile.bubbleFactory().createBubble(message, author, author.getPosition());
        Objects.requireNonNull(schedulable, "schedulable");

        ActiveBubble bubble = Objects.requireNonNull(schedulable.bubble(), "bubble");
        bubble.container().attach(ProfileComponent.key(), (context) -> profileFactory.create(context, profile));

        try {
            bubble.container().attachGroup(profile.componentFactories());
        } catch (RuntimeException | Error exception) {
            if (!bubble.isRemove()) {
                bubble.remove();
            }
            throw exception;
        }

        bubble = scheduler.put(schedulable);

        if (bubble == null) {
            if (!schedulable.bubble().isRemove()) {
                schedulable.bubble().remove();
            }
            throw new IllegalStateException("Created bubble cannot be scheduled");
        }

        return bubble;
    }

    public @Nullable ActiveBubble get(UUID uid) {
        return scheduler.get(uid);
    }

    public @Nullable ActiveBubble remove(UUID uid) {
        return scheduler.remove(uid);
    }

    public boolean contains(UUID uid) {
        return scheduler.contains(uid);
    }

    public synchronized Collection<ProfileComponent> getByProfileId(ProfileId id) {
        return profileContainer.get(id);
    }
}
