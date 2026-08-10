package me.thedivazo.messageoverhead.core.render.capability;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface TextCapability {
    Component getLine(int index);
    List<Component> getLines();
    void setLines(List<Component> components);
    void insertLine(int index, Component component);
    Component removeLine(int index);
    void setLine(int index, Component component);
    void addLine(Component component);
}
