package thedivazo.messageoverhead.channel.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import thedivazo.messageoverhead.channel.Channel;

@EqualsAndHashCode(callSuper = false)
public class SpecialChannel extends Channel {
    private static final SpecialChannel CommandChannel = new SpecialChannel(TypeSpecialChannel.COMMAND);
    private static final SpecialChannel PrivateChannel = new SpecialChannel(TypeSpecialChannel.PRIVATE);
    private static final SpecialChannel SpyChannel = new SpecialChannel(TypeSpecialChannel.SPY);
    private static final SpecialChannel AllChannel = new SpecialChannel(TypeSpecialChannel.ALL);

    @Getter
    private final TypeSpecialChannel typeSpecialChannelChannel;

    private SpecialChannel(TypeSpecialChannel typeSpecialChannelChannel) {
        super(typeSpecialChannelChannel.name());
        this.typeSpecialChannelChannel = typeSpecialChannelChannel;
    }

    @Override
    public boolean additionalEquals(Channel channel) {
        return channel.equals(this);
    }

    public static SpecialChannel getSpecialChannel(TypeSpecialChannel typeSpecialChannel) {
        switch (typeSpecialChannel) {
            case ALL: return AllChannel;
            case COMMAND: return CommandChannel;
            case PRIVATE: return PrivateChannel;
            case SPY: return PrivateChannel;
            default: return new SpecialChannel(typeSpecialChannel);
        }
    }
}
