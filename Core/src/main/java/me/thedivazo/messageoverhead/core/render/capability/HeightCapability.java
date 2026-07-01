package me.thedivazo.messageoverhead.core.render.capability;

public interface HeightCapability {
    double getHeight();
    interface Editable extends HeightCapability {
        void setHeight(double height);
    }
}
