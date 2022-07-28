package thedivazo.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import thedivazo.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BubbleMessage {

    private final List<Bubble> bubbleMessages = new ArrayList<>();
    private Location loc;
    private BukkitTask[] tasksRunnable = null;
    private boolean isBubbleInit = false;

    public BubbleMessage(Location loc) {
        this.loc = loc;
    }

    public void init(List<String> message) {
        if(isBubbleInit) return;
        List<String> resultMessages = null;

        for (String msg : message) {

            String noColorMessage = ColorString.toNoColorString(msg);

            List<String> msgLines = new ArrayList<>();
            for (int i = 0; i < Math.ceil((double) noColorMessage.length() / Main.getConfigPlugin().getSizeLine()); i++) {
                int begin = i * Main.getConfigPlugin().getSizeLine();
                int end = (i + 1) * Main.getConfigPlugin().getSizeLine();
                if (end > noColorMessage.length()) end = noColorMessage.length();
                msgLines.add(ColorString.substring(msg, begin, end));
            }

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
                                    msgLines.set(i, ((new StringBuffer(reverseLine.replaceFirst(Pattern.quote(last_letter), Matcher.quoteReplacement(newLine1))).reverse().toString()).trim()));
                                    msgLines.set(i + 1, (msgLines.get(i + 1).replaceFirst(Pattern.quote(line_2[0]), "")));
                                }
                            }
                        }
                    }
                }
            }
            if(resultMessages == null) resultMessages = msgLines;
            else {
                resultMessages.addAll(msgLines);
            }
        }
        ColorString.ofText(resultMessages);
        for (int i = 0; i < resultMessages.size(); i++) {
            if (resultMessages.get(resultMessages.size() - 1 - i).length() > 0) {
                if (!resultMessages.get(resultMessages.size() - 1 - i).equals(" ")) {
                    Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + i * 0.3D, loc.getZ());
                    this.bubbleMessages.add(new Bubble(resultMessages.get(resultMessages.size() - 1 - i), locBubble));
                }
            }
        }

    }



    public void show(Player player) {
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

    public void playParticle(Player player) {
        player.spawnParticle(Main.getConfigPlugin().getParticleType(), loc,
                Main.getConfigPlugin().getParticleCount(),
                Main.getConfigPlugin().getParticleOffsetX(),
                Main.getConfigPlugin().getParticleOffsetY(),
                Main.getConfigPlugin().getParticleOffsetZ());
    }

    public void removeTask(BukkitTask... tasksRunnable) {
        this.tasksRunnable = tasksRunnable;
    }

    public void playSound(Player player) {
        player.playSound(loc,
                Main.getConfigPlugin().getSoundType(),
                Main.getConfigPlugin().getSoundVolume(),
                Main.getConfigPlugin().getSoundPitch());
    }
}

