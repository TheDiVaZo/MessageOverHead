package thedivazo.bubble;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class BubbleWrapper {

    private final List<Bubble> bubbleMessages = new CopyOnWriteArrayList<>();
    private final Location loc;

    public BubbleWrapper(Location loc, List<String> message) {
        this.loc = loc;
        for (int i = 0; i < message.size(); i++) {
            if (message.get(message.size() - 1 - i).length() > 0 && !message.get(message.size() - 1 - i).equals(" ")) {
                Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.1D, loc.getZ());
                this.bubbleMessages.add(new Bubble(message.get(message.size() - 1 - i), locBubble));
            }
        }
    }

    public void show(Player player) {
        bubbleMessages.forEach(bubble -> bubble.show(player));
    }

    public void setPosition(Location position) {
        setPosition(position.getX(), position.getY(), position.getZ());
    }

    public void setPosition(double x, double y, double z) {
        for (int i = 0; i < bubbleMessages.size(); i++) {
            Location locBubble = new Location(loc.getWorld(), x, y + i * 0.3, z);
            bubbleMessages.get(i).setPosition(locBubble);
        }
    }

    public void remove() {
        bubbleMessages.forEach(Bubble::hideAll);
    }

    public void playParticle(Player player, Particle particle, int count, double offsetX, double offsetY, double offsetZ) {
        player.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ);
    }


    public void playSound(Player player, Sound sound, int volume, int pitch) {
        player.playSound(loc, sound, volume, pitch);
    }
}

