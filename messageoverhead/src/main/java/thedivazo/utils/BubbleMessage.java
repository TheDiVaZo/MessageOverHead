package thedivazo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class BubbleMessage {

    private final List<Bubble> bubbleMessages = new ArrayList<>();
    private Location loc;
    private BukkitTask[] tasksRunnable = null;
    private Config config;

    public BubbleMessage(List<String> messageLines, Location loc, Config config) {
        this.loc = loc;
        this.config = config;

        List<String> allMessages = null;

        for (String message : messageLines) {

            String noColorMessage = ColorString.toNoColorString(message);

            List<String> msgLines = new ArrayList<>();
            for (int i = 0; i < Math.ceil((double) noColorMessage.length() / config.getSizeLine()); i++) {
                int begin = i * config.getSizeLine();
                int end = (i + 1) * config.getSizeLine();
                if (end > noColorMessage.length()) end = noColorMessage.length();
                msgLines.add(ColorString.substring(message, begin, end));
            }

            //FIXED:
            for (int i = 0; i < msgLines.size(); i++) {
                if (msgLines.get(i).length() != 0) {
                    if (msgLines.get(i).charAt(msgLines.get(i).length() - 1) != ' ') {
                        if (i + 1 < msgLines.size()) {
                            if (msgLines.get(i + 1).charAt(0) != ' ') {
                                String[] line_1 = msgLines.get(i).split(" ");
                                String[] line_2 = msgLines.get(i + 1).split(" ");
                                String two_pieces = line_1[line_1.length - 1] + line_2[0];
                                if (ColorString.lengthString(line_2[0]) < 6) {
                                    String newLine1 = new StringBuffer(two_pieces).reverse().toString();
                                    String reverseLine = new StringBuffer(msgLines.get(i)).reverse().toString();
                                    String last_letter = new StringBuffer(line_1[line_1.length - 1]).reverse().toString();
                                    msgLines.set(i, (new StringBuffer(reverseLine.replaceFirst(Pattern.quote(last_letter), newLine1)).reverse().toString()).trim());
                                    msgLines.set(i + 1, msgLines.get(i + 1).replaceFirst(Pattern.quote(line_2[0]), ""));
                                }
                            }
                        }
                    }
                }
            }
            if(allMessages == null) allMessages = msgLines;
            else {
                allMessages.addAll(msgLines);
            }
        }
        ColorString.ofText(allMessages);
        for (int i = 0; i < allMessages.size(); i++) {
            if (allMessages.get(allMessages.size() - 1 - i).length() > 0) {
                if (!allMessages.get(allMessages.size() - 1 - i).equals(" ")) {
                    Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.3D, loc.getZ());
                    this.bubbleMessages.add(new Bubble(allMessages.get(allMessages.size() - 1 - i), locBubble));
                }
            }
        }
    }



    public void spawn(Player player) {
        for (Bubble msg : bubbleMessages) {
            msg.spawn(player);
        }
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
        if (tasksRunnable != null) {
            for (BukkitTask task : tasksRunnable) {
                task.cancel();
            }
        }

        bubbleMessages.forEach(Bubble::remove);
    }

    public void particle(Player player) {
        player.spawnParticle(config.getParticleType(), loc,
                config.getParticleCount(),
                config.getParticleOffsetX(),
                config.getParticleOffsetY(),
                config.getParticleOffsetZ());
    }

    public void removeTask(BukkitTask... tasksRunnable) {
        this.tasksRunnable = tasksRunnable;
    }

    public void sound(Player player) {
        player.playSound(loc,
                config.getSoundType(),
                config.getSoundVolume(),
                config.getSoundPitch());
    }
}

