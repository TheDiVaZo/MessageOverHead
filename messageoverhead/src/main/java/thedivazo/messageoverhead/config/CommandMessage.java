package thedivazo.messageoverhead.config;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import thedivazo.messageoverhead.util.text.DecoratedString;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class CommandMessage {
    private final DecoratedString access;

    public Optional<String> getAccess() {
        if (access == null || access.isEmpty()) return Optional.empty();
        else return Optional.of(access.getMinecraftColoredString());
    }
}
