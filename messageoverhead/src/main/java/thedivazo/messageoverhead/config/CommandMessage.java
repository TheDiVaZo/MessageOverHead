package thedivazo.messageoverhead.config;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.util.text.DecoratedString;
import thedivazo.messageoverhead.util.text.DecoratedStringUtils;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class CommandMessage {
    private final DecoratedString access;

    public Optional<String> getAccess() {
        if (access == null || access.isEmpty()) return Optional.empty();
        else return Optional.of(access.getMinecraftColoredString());
    }

    public Optional<String> getAccess(OfflinePlayer player) {
        if (access == null || access.isEmpty()) return Optional.empty();
        else return Optional.of(DecoratedStringUtils.insertPlaceholders(access, player).getMinecraftColoredString());
    }
}
