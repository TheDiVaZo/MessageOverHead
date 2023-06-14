package thedivazo.bubble;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BubbleWrapper {

    private final List<Bubble> bubbleMessages = new CopyOnWriteArrayList<>();
    private volatile Location loc;

    private final Set<Player> showers = ConcurrentHashMap.newKeySet();

    public BubbleWrapper(Location loc, List<String> message) {
        this.loc = loc;
        for (int i = 0; i < message.size(); i++) {
            if (message.get(message.size() - 1 - i).length() > 0 && !message.get(message.size() - 1 - i).equals(" ")) {
                Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.1D, loc.getZ());
                this.bubbleMessages.add(new Bubble(message.get(message.size() - 1 - i), locBubble));
            }
        }
    }

    public synchronized void show(Player... players) {
        showers.addAll(List.of(players));
        bubbleMessages.forEach(bubble -> bubble.show(players));
    }

    public synchronized void setPosition(Location position) {
        this.loc = position;
        for (int i = 0; i < bubbleMessages.size(); i++) {
            Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.3, loc.getZ());
            bubbleMessages.get(i).setPosition(locBubble);
        }
    }

    public void remove() {
        bubbleMessages.forEach(bubble -> bubble.hide(showers.toArray(Player[]::new)));
        showers.clear();
    }

    public void playParticle(Player player, Particle particle, int count, double offsetX, double offsetY, double offsetZ) {
        player.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ);
    }


    public void playSound(Player player, Sound sound, int volume, int pitch) {
        player.playSound(loc, sound, volume, pitch);
    }
}

