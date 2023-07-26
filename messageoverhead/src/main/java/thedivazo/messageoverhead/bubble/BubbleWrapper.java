package thedivazo.messageoverhead.bubble;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.config.BubbleModel;
import thedivazo.messageoverhead.utils.text.DecoratedString;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BubbleWrapper {
    @Getter
    private final BubbleModel bubbleModel;
    private final List<Bubble> bubbleMessages = new CopyOnWriteArrayList<>();
    @Getter
    private final int textLength;
    private Location loc;

    private final Set<Player> showers = ConcurrentHashMap.newKeySet();

    public BubbleWrapper(Location loc, BubbleModel bubbleModel, List<DecoratedString> message) {
        this.loc = loc;
        this.bubbleModel = bubbleModel;
        int textLength = 0;
        for (int i = 0; i < message.size(); i++) {
            DecoratedString line = message.get(message.size() - 1 - i);
            if (line.length() > 0 && !line.getNoColorString().equals(" ")) {
                textLength += line.length();
                Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.3 + bubbleModel.getBiasY(), loc.getZ());
                this.bubbleMessages.add(new Bubble(line, locBubble));
            }
        }
        this.textLength = textLength;
    }

    public void show(Player... players) {
        Set<Player> playerSet = Stream.of(players).filter(player->!showers.contains(player)).collect(Collectors.toSet());
        showers.addAll(playerSet);
        bubbleMessages.forEach(bubble -> bubble.show(playerSet));
    }

    public void show(Set<Player> players) {
        Set<Player> filteredPlayers = players.stream().filter(player->!showers.contains(player)).collect(Collectors.toSet());
        showers.addAll(filteredPlayers);
        bubbleMessages.forEach(bubble -> bubble.show(filteredPlayers));
    }

    public void setPosition(Location position) {
        this.loc = position;
        for (int i = 0; i < bubbleMessages.size(); i++) {
            Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.3 + bubbleModel.getBiasY(), loc.getZ());
            bubbleMessages.get(i).setPosition(locBubble, showers);
        }
    }

    public void remove() {
        bubbleMessages.forEach(bubble -> bubble.hide(showers.toArray(Player[]::new)));
        showers.clear();
    }

    public void playParticle(Particle particle, int count, double offsetX, double offsetY, double offsetZ, Player... players) {
        for (Player player : players) {
            player.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ);
        }
    }

    public void playParticle(Particle particle, int count, double offsetX, double offsetY, double offsetZ, Set<Player> players) {
        for (Player player : players) {
            player.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ);
        }
    }

    public void playSound(Sound sound, int volume, int pitch, Player... players) {
        for (Player player : players) {
            player.playSound(loc, sound, volume, pitch);
        }
    }

    public void playSound(Sound sound, int volume, int pitch, Set<Player> players) {
        for (Player player : players) {
            player.playSound(loc, sound, volume, pitch);
        }
    }

}

