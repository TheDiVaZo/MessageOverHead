package me.thedivazo.messageoverhead.core.component.scope;

public interface ComponentScoped<C> {
     void onTick(C context);
}
