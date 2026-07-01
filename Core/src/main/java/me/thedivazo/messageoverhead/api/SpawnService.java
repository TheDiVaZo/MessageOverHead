package me.thedivazo.messageoverhead.api;

import me.thedivazo.messageoverhead.annotation.MainThread;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.Author;
import me.thedivazo.messageoverhead.core.BubbleContainer;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.component.ProfileComponent;
import me.thedivazo.messageoverhead.core.component.ProfileComponentIndex;
import me.thedivazo.messageoverhead.core.event.EventBus;
import me.thedivazo.messageoverhead.core.event.SpawnBubbleEvent;
import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.core.tick.SchedulableBubble;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.ProfileId;
import me.thedivazo.messageoverhead.profile.ProfileRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@MainThread
public final class SpawnService {
    private final BubbleScheduler scheduler;
    private final BubbleContainer bubbleContainer;
    private final ProfileComponentIndex profileIndex;
    private final ProfileRegistry registry;
    private final EventBus eventBus;

    public SpawnService(
            BubbleScheduler scheduler,
            BubbleContainer bubbleContainer,
            ProfileComponentIndex profileIndex,
            ProfileRegistry registry,
            EventBus eventBus
    ) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.bubbleContainer = Objects.requireNonNull(bubbleContainer, "bubbleContainer");
        this.profileIndex = Objects.requireNonNull(profileIndex, "profileIndex");
        this.registry = Objects.requireNonNull(registry, "registry");
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    public ActiveBubble spawn(Message message, Author author, ProfileId profileId) {
        Objects.requireNonNull(profileId, "profileId");

        BubbleProfile profile = registry.find(profileId);
        if (profile == null) {
            throw new IllegalArgumentException("Unknown profile id: " + profileId);
        }

        return spawn(message, author, profile);
    }

    public ActiveBubble spawn(Message message, Author author, BubbleProfile profile) {
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(author, "author");
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(profile.id(), "profile.id");
        Objects.requireNonNull(profile.bubbleFactory(), "profile.bubbleFactory");
        Objects.requireNonNull(profile.componentFactories(), "profile.componentFactories");
        if (profile.componentFactories().containsKey(ProfileComponent.key())) {
            throw new IllegalArgumentException(
                    "Profile component is managed by SpawnService and cannot be included in profile " + profile.id()
            );
        }

        SchedulableBubble schedulable = profile.bubbleFactory().createBubble(message, author, author.getPosition());
        Objects.requireNonNull(schedulable, "schedulable");

        ActiveBubble bubble = Objects.requireNonNull(schedulable.bubble(), "bubble");

        try {
            ProfileComponent profileComponent = bubble.container().attach(
                    ProfileComponent.key(),
                    ProfileComponent.factory(profileIndex, profile)
            );
            if (profileComponent == null) {
                throw new IllegalStateException("Profile component cannot be attached");
            }

            Collection<?> attachedComponents = bubble.container().attachGroup(profile.componentFactories());
            if (attachedComponents == null || attachedComponents.size() != profile.componentFactories().size()) {
                throw new IllegalStateException("Profile components cannot be attached");
            }

            ActiveBubble scheduledBubble = scheduler.put(schedulable);
            if (scheduledBubble == null) {
                throw new IllegalStateException("Created bubble cannot be scheduled");
            }

            ActiveBubble indexedBubble = bubbleContainer.put(scheduledBubble);
            if (indexedBubble == null) {
                scheduler.remove(scheduledBubble.id());
                throw new IllegalStateException("Scheduled bubble cannot be indexed");
            }
            eventBus.post(new SpawnBubbleEvent(indexedBubble));

            return scheduledBubble;
        } catch (RuntimeException | Error exception) {
            if (!bubble.isRemove()) {
                bubble.remove();
            }
            throw exception;
        }
    }

    public @Nullable ActiveBubble get(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        return scheduler.get(uid);
    }

    public @Nullable ActiveBubble getActualByActorId(UUID uid) {
        Objects.requireNonNull(uid, "uid");
        return bubbleContainer.getLastBubble(uid);
    }

    public Collection<ActiveBubble> getOldByActorId(UUID uid) {
        Objects.requireNonNull(uid, "uid");
        return bubbleContainer.getOldBubbles(uid);
    }

    public @Nullable ActiveBubble remove(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        try {
            return scheduler.remove(uid);
        } finally {
            bubbleContainer.remove(uid);
        }
    }

    public boolean contains(UUID uid) {
        Objects.requireNonNull(uid, "uid");

        return scheduler.contains(uid);
    }

    public void clear() {
        try {
            scheduler.clear();
        } finally {
            bubbleContainer.clear();
            profileIndex.clear();
        }
    }

    public Collection<ProfileComponent> getByProfileId(ProfileId id) {
        return profileIndex.get(id);
    }
}
