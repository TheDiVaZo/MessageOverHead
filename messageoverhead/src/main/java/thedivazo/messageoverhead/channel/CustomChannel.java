package thedivazo.messageoverhead.channel;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class CustomChannel extends Channel {

    public CustomChannel(String name) {
        super(name);
    }

    @Override
    public boolean additionalEquals(Channel channel) {
        return channel instanceof CustomChannel && channel.getName().equals(this.name);
    }
}
