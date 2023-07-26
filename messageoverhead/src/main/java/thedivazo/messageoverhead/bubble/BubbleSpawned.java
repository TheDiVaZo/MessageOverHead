package thedivazo.messageoverhead.bubble;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.api.logging.Logger;
import thedivazo.messageoverhead.config.BubbleModel;

import java.util.Set;

public class BubbleSpawned {
    private final BubbleWrapper bubbleWrapper;
    private final BukkitTask positionTask;
    private final BukkitTask removeTask;


    public BubbleSpawned(BubbleWrapper bubbleWrapper, Player ownerPlayer, Set<Player> showers) {
        this.bubbleWrapper = bubbleWrapper;
        BubbleModel bubbleModel = bubbleWrapper.getBubbleModel();
        if (bubbleModel.isVisibleTextForOwner()) bubbleWrapper.show(ownerPlayer);
        bubbleWrapper.show(showers);
        if (bubbleModel.getParticleModel().isEnable()) {
            BubbleModel.ParticleModel particleModel = bubbleModel.getParticleModel();
            bubbleWrapper.playParticle(particleModel.getParticle(), particleModel.getCount(), particleModel.getOffsetX(), particleModel.getOffsetY(), particleModel.getOffsetZ(), showers);
        }
        if (bubbleModel.getSoundModel().isEnable()) {
            BubbleModel.SoundModel soundModel = bubbleModel.getSoundModel();
            bubbleWrapper.playSound(soundModel.getSound(), soundModel.getPitch(), soundModel.getVolume(), showers);
        }
        positionTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MessageOverHead.getInstance(), ()-> {
            bubbleWrapper.setPosition(ownerPlayer.getLocation());
        }, 0, 1);
        removeTask = Bukkit.getScheduler().runTaskLaterAsynchronously(MessageOverHead.getInstance(), ()-> {
            positionTask.cancel();
            bubbleWrapper.remove();
        }, Math.round(getLifeTimeBubble()*20));
    }

    public void remove() {
        if (!positionTask.isCancelled() || !removeTask.isCancelled()) {
            positionTask.cancel();
            removeTask.cancel();
            bubbleWrapper.remove();
        }

    }

    public double getLifeTimeBubble() {
        BubbleModel.LifeTimeModel lifeTimeModel = bubbleWrapper.getBubbleModel().getLifeTimeModel();
        double currentLifeTime = lifeTimeModel.getTimePerChar() * bubbleWrapper.getTextLength();
        return Math.min(Math.max(currentLifeTime, lifeTimeModel.getMinTime()), lifeTimeModel.getMaxTime());
    }
}
