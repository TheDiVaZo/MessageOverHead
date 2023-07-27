package thedivazo.messageoverhead.channel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelFactory {
    private static Pattern pattern = Pattern.compile("#(.+)");

    public static Channel create(String name) {
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) return new Channel(matcher.group(), Type.CUSTOM_CHANNEL);
        switch (name.toLowerCase()) {
            case "all": return new Channel(null, Type.ALL);
            case "private": return new Channel(null, Type.PRIVATE);
            case "command": return new Channel(null, Type.COMMAND);
            default: throw new IllegalArgumentException("The channel name '"+name+"' is incorrect.");
        }
    }
}
