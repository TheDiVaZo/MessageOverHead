package thedivazo.messageoverhead.bubble;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.messageoverhead.MessageOverHead;
import thedivazo.messageoverhead.config.BubbleModel;

import java.util.Set;
import java.util.stream.Collectors;

public class BubbleSpawned {

    private final BubbleWrapper bubbleWrapper;

    @Getter
    private final BubbleModel bubbleModel;
    private BukkitTask positionTask;
    private final BukkitTask removeTask;

    @Getter
    private final Player sender;

    @Getter
    private boolean removed = false;

    private Set<Player> filterShowers(Player sender, Set<Player> showers) {
        return showers.stream().filter(player ->
                sender.getWorld().equals(player.getWorld())
                && sender.getLocation().distance(player.getLocation()) <= bubbleWrapper.getBubbleModel().getDistance()
                ).collect(Collectors.toSet());
    }


    public BubbleSpawned(BubbleWrapper bubbleWrapper, Player sender, Set<Player> showers) {
        this.bubbleWrapper = bubbleWrapper;
        this.sender = sender;
        this.bubbleModel = bubbleWrapper.getBubbleModel();
        BubbleModel bubbleModel = bubbleWrapper.getBubbleModel();
        if (bubbleModel.isVisibleTextForOwner()) bubbleWrapper.show(sender);
        Set<Player> filteredShowers = filterShowers(sender, showers);
        bubbleWrapper.show(filteredShowers);
        if (bubbleModel.getParticleModel().isEnable()) {
            BubbleModel.ParticleModel particleModel = bubbleModel.getParticleModel();
            bubbleWrapper.playParticle(particleModel.getParticle(), particleModel.getCount(), particleModel.getOffsetX(), particleModel.getOffsetY(), particleModel.getOffsetZ(), showers);
        }
        if (bubbleModel.getSoundModel().isEnable()) {
            BubbleModel.SoundModel soundModel = bubbleModel.getSoundModel();
            bubbleWrapper.playSound(soundModel.getSound(), soundModel.getPitch(), soundModel.getVolume(), filteredShowers);
        }
        positionTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MessageOverHead.getInstance(), ()-> {
            bubbleWrapper.setPosition(sender.getLocation());
        }, 0, 1);
        removeTask = Bukkit.getScheduler().runTaskLaterAsynchronously(MessageOverHead.getInstance(), ()-> {
            positionTask.cancel();
            bubbleWrapper.remove();
            removed = true;
        }, Math.round(getLifeTimeBubble()*20));
    }

    public void remove() {
        if (!positionTask.isCancelled() || !removeTask.isCancelled() || !removed) {
            positionTask.cancel();
            removeTask.cancel();
            bubbleWrapper.remove();
            removed = true;
        }

    }

    public double getLifeTimeBubble() {
        BubbleModel.LifeTimeModel lifeTimeModel = bubbleWrapper.getBubbleModel().getLifeTimeModel();
        double currentLifeTime = lifeTimeModel.getTimePerChar() * bubbleWrapper.getTextLength();
        return Math.min(Math.max(currentLifeTime, lifeTimeModel.getMinTime()), lifeTimeModel.getMaxTime());
    }
}
