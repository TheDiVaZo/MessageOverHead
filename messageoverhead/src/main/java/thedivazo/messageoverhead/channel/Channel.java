package thedivazo.messageoverhead.channel;

import lombok.Getter;
import thedivazo.messageoverhead.channel.special.SpecialChannel;
import thedivazo.messageoverhead.channel.special.TypeSpecialChannel;


public abstract class Channel {

    @Getter
    protected String name;

    protected Channel(String name) {
        this.name = name;
    }

    public boolean compatibilityChannel(Channel channel) {
        return channel instanceof SpecialChannel && ((SpecialChannel) channel).getTypeSpecialChannelChannel().equals(TypeSpecialChannel.ALL) || additionalEquals(channel);
    }

    protected abstract boolean additionalEquals(Channel channel);
}
