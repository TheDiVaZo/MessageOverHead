package thedivazo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BubbleMessage {

    List<Bubble> BubbleMessages = new ArrayList<>();
    Location loc;
    BukkitTask[] Tasks = null;

    public BubbleMessage(String message, Location loc) {
        this.loc = loc;
        List<String> NewLines = new ArrayList<>();
        List<String> lines = Arrays.asList(message.split(" "));
        //Bukkit.getLogger().warning(Arrays.toString(lines.toArray()));
        StringBuilder strBuild = new StringBuilder("");
        int sizeColor = 0;
        String colorOld = "";
        for (int i = 0; i < lines.size(); i++) {
            String line = colorOld + lines.get(i);
            List<String> colors = new ArrayList<>();
            Matcher colorMatcher = Pattern.compile("" + ChatColor.COLOR_CHAR + "[0-9a-zA-Z]").matcher(line);
            while (colorMatcher.find()) {
                colors.add(colorMatcher.group());
            }
            //Bukkit.getLogger().warning(Arrays.toString(colors.toArray()));
            //Bukkit.getLogger().warning(String.join("",colors.toArray(new String[0])));
            sizeColor += colors.size() * 2;
            strBuild.append(line + " ");
            //Bukkit.getLogger().warning(colorOld);
            if ((strBuild.length() - sizeColor) > 24) {
                NewLines.add(strBuild.toString());
                strBuild.setLength(0);
                sizeColor = 0;
            }
            colorOld = String.join("", colors.toArray(new String[0]));
        }
        if (strBuild.length() - sizeColor * 2 != 0) {
            NewLines.add(colorOld + strBuild.toString());
        }


        for (int i = 0; i < NewLines.size(); ++i) {
            Location locBubble = new Location(loc.getWorld(), loc.getX(), loc.getY() + (double) i * 0.3D, loc.getZ());
            this.BubbleMessages.add(new Bubble(NewLines.get(NewLines.size() - 1 - i), locBubble));
        }

    }

    public void spawn(int distance) {
        for (Bubble msg : BubbleMessages) {
            msg.spawn(distance);
        }
    }

    public void spawn(Player player) {
        for (Bubble msg : BubbleMessages) {
            msg.spawn(player);
        }
    }

    public void setPosition(Location position) {
        setPosition(position.getX(), position.getY(), position.getZ());
    }

    public void setPosition(double x, double y, double z) {
        for (int i = 0; i < BubbleMessages.size(); i++) {
            Location locBubble = new Location(loc.getWorld(), x, y + i * 0.3, z);
            BubbleMessages.get(i).setPosition(locBubble);
        }
    }

    public void remove() {
        if (Tasks != null) {
            for (BukkitTask task : Tasks) {
                task.cancel();
            }
        }
        for (int i = 0; i < BubbleMessages.size(); i++) {
            BubbleMessages.get(i).remove();
        }
    }

    public void particle(int distance) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(loc) <= distance) {
                Location locPart = loc.clone();
                player.spawnParticle(Main.particleType, loc, Main.particleCount, Main.particleOffsetX, Main.particleOffsetY, Main.particleOffsetZ);
            }
        }
    }

    public void removeTask(BukkitTask... Task) {
        this.Tasks = Task;
    }

    public void sound(int distance) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(loc) <= distance) {
                player.playSound(loc, Main.soundType, Main.soundVolume, Main.soundPitch);
            }
        }
    }
}

