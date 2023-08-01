package thedivazo.messageoverhead.channel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import thedivazo.messageoverhead.channel.special.SpecialChannel;
import thedivazo.messageoverhead.channel.special.TypeSpecialChannel;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelFactory {

    private static final Map<String, TypeSpecialChannel> nameMap = Map.of(
            "#private", TypeSpecialChannel.PRIVATE,
            "#command", TypeSpecialChannel.COMMAND,
            "#all", TypeSpecialChannel.ALL);

    public static Channel create(String name) {
        return nameMap.containsKey(name) ? SpecialChannel.getSpecialChannel(nameMap.get(name)) : new CustomChannel(name);
    }
}
