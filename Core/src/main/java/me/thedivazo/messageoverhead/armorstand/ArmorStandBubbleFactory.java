package me.thedivazo.messageoverhead.armorstand;

import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.core.Message;
import me.thedivazo.messageoverhead.core.render.RendererBubble;
import me.thedivazo.messageoverhead.core.render.RendererFactory;
import me.thedivazo.messageoverhead.util.Positionc;

public class ArmorStandBubbleFactory implements RendererFactory {

    private final double lineSpacing;

    public ArmorStandBubbleFactory(double lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    @Override
    public RendererBubble create(Message message, Positionc positionc) {
        GroupedArmorStand armorStand = new GroupedArmorStand(
                message.lines(),
                FakeArmorStand.FACTORY,
                lineSpacing,
                positionc,
                MessageOverHeadPlugin.SERVER_VERSION
        );
        return new BubbleArmorStand(message, armorStand, positionc);
    }
}
