package thedivazo.messageoverhead.integration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.listener.chat.*;
import thedivazo.messageoverhead.listener.vanish.*;
import thedivazo.messageoverhead.logging.Logger;
import thedivazo.messageoverhead.vanish.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegrationManager {
    private static final Map<String, Boolean> cashPluginEnabled = new HashMap<>();

    public static boolean isPlugin(String name) {
        boolean pluginEnabled = Bukkit.getPluginManager().getPlugin(name) != null;
        if (pluginEnabled) Logger.info(name + " plugin has been connect");
        return pluginEnabled;
    }

    public static void clearCash() {
        cashPluginEnabled.clear();
    }

    public static boolean isPlaceholderAPI() {
        return isPlugin("PlaceholderAPI");
    }

    public static boolean isProtocolLib() {
        return isPlugin("ProtocolLib");
    }

    public static boolean isItemsAdder() {
        return isPlugin("ItemsAdder");
    }

    public static boolean isOraxen() {
        return isPlugin("Oraxen");
    }

    public static boolean isChatty() {
        return isPlugin("Chatty");
    }

    public static boolean isChatControlRed() {
        return isPlugin("ChatControlRed");
    }

    public static boolean isVentureChat() {
        return isPlugin("VentureChat");
    }

    public static boolean isEssentialsX() {
        return isPlugin("EssentialsX");
    }

    public static boolean isCMI() {
        return isPlugin("CMI");
    }

    public static boolean isSuperVanish() {
        return isPlugin("SuperVanish");
    }

    public static Set<Listener> getChatListeners() {
        Set<Listener> listenerSet = new HashSet<>();
        if (isChatControlRed()) listenerSet.add(new ChatControlRedListener());
        if (isChatty()) listenerSet.add(new ChattyListener());
        if (isVentureChat()) listenerSet.add(new VentureChatListener());
        if (isEssentialsX()) listenerSet.add(new EssentialsXChatListener());
        if (listenerSet.isEmpty())
            listenerSet.add(new DefaultChatListener());
        return listenerSet;
    }

    public static Set<Listener> getVanishListeners() {
        Set<Listener> listenerSet = new HashSet<>();
        if (isCMI()) listenerSet.add(new CMIVanishListener());
        if (isEssentialsX()) listenerSet.add(new EssentialsXVanishListener());
        if (isSuperVanish()) listenerSet.add(new SuperVanishListener());
        listenerSet.add(new DefaultVanishListener());
        return listenerSet;
    }

    public static Set<VanishManager> getVanishManagers() {
        Set<VanishManager> managerSet = new HashSet<>();
        if (isEssentialsX()) managerSet.add(new EssentialsXVanishManager());
        if (isCMI()) managerSet.add(new CMIVanishManager());
        if (isSuperVanish()) managerSet.add(new SuperVanishManager());
        managerSet.add(new PotionVanishManager());
        managerSet.add(new GameModeVanishManager());
        managerSet.add(new BubbleActiveStatusVanishManager());
        return managerSet;
    }
}
