package thedivazo.messageoverhead.integration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import thedivazo.messageoverhead.listener.chat.*;
import thedivazo.messageoverhead.listener.vanish.*;
import thedivazo.messageoverhead.vanish.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegrationManager {
    private static final Map<String, Boolean> cashPluginEnabled = new HashMap<>();

    public static boolean isPlugin(String name) {
        if (cashPluginEnabled.containsKey(name)) return cashPluginEnabled.get(name);
        boolean pluginEnabled = Bukkit.getPluginManager().isPluginEnabled(name);
        cashPluginEnabled.put(name, pluginEnabled);
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

    public static boolean isChatControl() {
        return isPlugin("ChatControl");
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
        if (isChatControl()) listenerSet.add(new ChatControlListener());
        if (isChatty()) listenerSet.add(new ChattyListener());
        if (isVentureChat()) listenerSet.add(new VentureChatListener());
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
