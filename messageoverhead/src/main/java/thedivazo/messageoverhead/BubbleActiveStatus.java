package thedivazo.messageoverhead;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;

public class BubbleActiveStatus {
    public enum Status {
        ENABLED,
        DISABLED
    }

    private static Map<OfflinePlayer, Status> statusMap = new HashMap<>();

    public static Status getStatus(OfflinePlayer player) {
        if (statusMap.containsKey(player)) return statusMap.get(player);
        Status status = Status.ENABLED;
        statusMap.put(player, status);
        return status;
    }

    public static void setStatus(OfflinePlayer player, Status status) {
        statusMap.put(player, status);
    }
}
