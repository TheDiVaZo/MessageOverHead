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
    private final List<Bubble> bubbleMessages = new CopyOnWriteArrayList<>();
    @Getter
    private final int textLength;
    private Location loc;

    private final Set<Player> showers = ConcurrentHashMap.newKeySet();

    @Getter
    private boolean hided = true;

    @Getter
    private boolean removed = false;

    public BubbleWrapper(Location loc, List<DecoratedString> message) {
        this.loc = loc;
        int tempTextLength = 0;
        for (int i = 0; i < message.size(); i++) {
            DecoratedString line = message.get(message.size() - 1 - i);
            if (line.length() > 0 && !line.getNoColorString().equals(" ")) {
                tempTextLength += line.length();
                Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.3, loc.getZ());
                this.bubbleMessages.add(new BubbleArmorStand(line.getMinecraftColoredString(), locBubble));
            }
        }
        this.textLength = tempTextLength;
    }

    public void show(Player... players) {
        if (isRemoved() || !isHided()) return;
        Set<Player> playerSet = Stream.of(players).filter(player->!showers.contains(player)).collect(Collectors.toSet());
        showers.addAll(playerSet);
        bubbleMessages.forEach(bubble -> bubble.show(playerSet));
        hided = false;
    }

    public void show() {
        if (isRemoved() || !isHided()) return;
        bubbleMessages.forEach(bubble -> bubble.show(showers));
        hided = false;
    }

    public void show(Set<Player> players) {
        if (isRemoved() || !isHided()) return;
        Set<Player> filteredPlayers = players.stream().filter(player->!showers.contains(player)).collect(Collectors.toSet());
        showers.addAll(filteredPlayers);
        bubbleMessages.forEach(bubble -> bubble.show(filteredPlayers));
        hided = false;
    }

    public void setPosition(Location position) {
        if (isRemoved()) return;
        this.loc = position;
        for (int i = 0; i < bubbleMessages.size(); i++) {
            Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.3, loc.getZ());
            bubbleMessages.get(i).setPosition(locBubble, showers);
        }
    }

    public void hide() {
        if (isRemoved() || isHided()) return;
        bubbleMessages.forEach(bubble -> bubble.hide(showers));
        hided = true;
    }

    public void remove() {
        if(isRemoved()) return;
        hide();
        clearShowers();
        removed = true;
    }

    public void clearShowers() {
        if (isRemoved()) return;
        showers.clear();
    }

    public void addShowers(Set<Player> players) {
        showers.addAll(players);
    }

    public void addShower(Player player) {
        showers.add(player);
    }

    public void playParticle(Particle particle, int count, double offsetX, double offsetY, double offsetZ) {
        if (isRemoved() || isHided()) return;
        for (Player player : showers) {
            player.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ);
        }
    }

    public void playSound(Sound sound, int volume, int pitch) {
        if (isRemoved() || isHided()) return;
        for (Player player : showers) {
            player.playSound(loc, sound, volume, pitch);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BubbleWrapper)) return false;
        BubbleWrapper that = (BubbleWrapper) o;
        return getTextLength() == that.getTextLength() && getBubbleMessages().equals(that.getBubbleMessages());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBubbleMessages(), getTextLength());
    }
}

