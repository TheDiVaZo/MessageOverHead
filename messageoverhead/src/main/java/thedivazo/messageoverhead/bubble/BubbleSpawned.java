package thedivazo.messageoverhead.bubble;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.messageoverhead.MessageOverHead;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BubbleSpawned {

    private final BubbleWrapper bubbleWrapper;

    @Getter
    private final BubbleModel bubbleModel;
    private BukkitTask positionTask;
    private BukkitTask removeTask;

    @Getter
    private final Player sender;

    private Set<Player> filterShowers(Player sender, Set<Player> showers) {
        return showers.stream().filter(player ->
                sender.getWorld().equals(player.getWorld())
                && sender.getLocation().distance(player.getLocation()) <= bubbleModel.getDistance()
                ).collect(Collectors.toSet());
    }
    
    private Location getLocationPlusBiasY(Player sender) {
        Location location = sender.getLocation();
        location.setY(location.getY()+bubbleModel.getBiasY());
        return location;
    }

    public void playParticle() {
        if (bubbleModel.getParticleModel().isEnable() && !bubbleWrapper.isHided()) {
            BubbleModel.ParticleModel particleModel = bubbleModel.getParticleModel();
            bubbleWrapper.playParticle(particleModel.getParticle(), particleModel.getCount(), particleModel.getOffsetX(), particleModel.getOffsetY(), particleModel.getOffsetZ());
        }
    }

    public void playSound() {
        if (bubbleModel.getSoundModel().isEnable() && !bubbleWrapper.isHided()) {
            BubbleModel.SoundModel soundModel = bubbleModel.getSoundModel();
            bubbleWrapper.playSound(soundModel.getSound(), soundModel.getPitch(), soundModel.getVolume());
        }
    }


    public BubbleSpawned(BubbleModel bubbleModel, BubbleWrapper bubbleWrapper, Player sender, Set<Player> showers) {
        this.bubbleModel = bubbleModel;
        this.bubbleWrapper = bubbleWrapper;
        this.sender = sender;
        if (bubbleModel.isVisibleTextForOwner()) bubbleWrapper.addShower(sender);
        Set<Player> filteredShowers = filterShowers(sender, showers);
        bubbleWrapper.addShowers(filteredShowers);
        if (BubbleManager.getVisiblePredicate().test(sender)) {
            show();
            playParticle();
            playSound();
        }
        positionTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MessageOverHead.getInstance(), ()-> {
            bubbleWrapper.setPosition(getLocationPlusBiasY(sender));
        }, 0, 1);
        removeTask = Bukkit.getScheduler().runTaskLaterAsynchronously(MessageOverHead.getInstance(), ()-> {
            positionTask.cancel();
            bubbleWrapper.remove();
        }, Math.round(getLifeTimeBubble()*20));
    }

    public void remove() {
        positionTask.cancel();
        removeTask.cancel();
        bubbleWrapper.remove();
    }

    public void hide() {
        bubbleWrapper.hide();
    }

    public void show() {
        bubbleWrapper.show();
    }

    public double getLifeTimeBubble() {
        BubbleModel.LifeTimeModel lifeTimeModel = bubbleModel.getLifeTimeModel();
        double currentLifeTime = lifeTimeModel.getTimePerChar() * bubbleWrapper.getTextLength();
        return Math.min(Math.max(currentLifeTime, lifeTimeModel.getMinTime()), lifeTimeModel.getMaxTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BubbleSpawned)) return false;
        BubbleSpawned that = (BubbleSpawned) o;
        return bubbleWrapper.equals(that.bubbleWrapper) && getBubbleModel().equals(that.getBubbleModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(bubbleWrapper, getBubbleModel());
    }
}
