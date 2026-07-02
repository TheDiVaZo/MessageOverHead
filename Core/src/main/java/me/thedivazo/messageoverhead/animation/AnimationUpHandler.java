package me.thedivazo.messageoverhead.animation;

import me.thedivazo.messageoverhead.api.SpawnService;
import me.thedivazo.messageoverhead.core.ActiveBubble;
import me.thedivazo.messageoverhead.core.event.Handler;
import me.thedivazo.messageoverhead.core.event.SpawnBubbleEvent;
import me.thedivazo.messageoverhead.core.render.capability.HeightCapability;

import java.util.Collection;
import java.util.UUID;

public class AnimationUpHandler implements Handler<SpawnBubbleEvent> {
    private static final double HEIGHT_PADDING = 0.05;

    private final SpawnService service;
    public AnimationUpHandler(SpawnService service) {
        this.service = service;
    }

    @Override
    public void handle(SpawnBubbleEvent event) {
        UUID actorId = event.bubble().author().getUID();

        if (!event.bubble().capabilities().hasCapability(HeightCapability.class)) return;

        HeightCapability heightCapability = event.bubble().capabilities().requireCapability(HeightCapability.class);

        Collection<ActiveBubble> prevBubbles = service.getOldByActorId(actorId);
        double offsetY = heightCapability.getHeight() + HEIGHT_PADDING;
        for (ActiveBubble bubble : prevBubbles) {
            updateAnimationOffset(bubble, offsetY);
        }
    }

    private void updateAnimationOffset(ActiveBubble bubble, double offsetY) {
        AnimationOffsetComponentScoped animationOffset = AnimationOffsetComponentScoped.getOrAttach(bubble);
        if (animationOffset == null) {
            return;
        }

        animationOffset.setTargetOffset(0, animationOffset.targetOffsetY() + offsetY, 0);
    }
}
