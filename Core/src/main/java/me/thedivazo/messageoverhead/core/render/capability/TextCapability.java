package me.thedivazo.messageoverhead.core.render.capability;

import net.kyori.adventure.text.Component;

public interface TextCapability {
    Component text();

    interface Editable extends TextCapability {
        void setText(Component text);
    }
}
