package thedivazo.messageoverhead.bubble;

import lombok.AllArgsConstructor;
import org.bukkit.GameMode;
import thedivazo.messageoverhead.BubbleActiveStatus;
import thedivazo.messageoverhead.integration.IntegrationManager;
import thedivazo.messageoverhead.logging.Logger;
import lombok.Getter;
import org.bukkit.entity.Player;
import thedivazo.messageoverhead.channel.Channel;
import thedivazo.messageoverhead.vanish.VanishManager;

import java.util.*;
import java.util.function.Predicate;

public class BubbleManager {
    private final Map<Player, BubbleSpawned> bubbles = new HashMap<>();

    @Getter
    private static VanishManagerSet vanishManagers = new VanishManagerSet(IntegrationManager.getVanishManagers());

    @Getter
    private static Predicate<Player> noSpectatorPredicate = player -> !player.getGameMode().equals(GameMode.SPECTATOR);
    @Getter
    private static Predicate<Player> statusEnabledPredicate = player -> BubbleActiveStatus.getStatus(player).equals(BubbleActiveStatus.Status.ENABLED);
    @Getter
    private static Predicate<Player> visiblePredicate = player -> vanishManagers.visibleForAll().test(player);

    @Getter
    private static Predicate<Player> showPredicate = player -> visiblePredicate.and(noSpectatorPredicate).and(statusEnabledPredicate).test(player);
    public Optional<BubbleSpawned> getBubbleSpawned(Player player) {
        return Optional.ofNullable(bubbles.get(player));
    }

    @Getter
    private final BubbleGeneratorManager bubbleGeneratorManager;

    public BubbleManager(BubbleGeneratorManager bubbleGeneratorManager) {
        this.bubbleGeneratorManager = bubbleGeneratorManager;
    }

    public void spawnBubble(String playerText, BubbleGenerator bubbleGenerator, Player sender, Set<Player> showers) {
        if (bubbles.containsKey(sender)) bubbles.get(sender).remove();
        bubbles.put(sender,bubbleGenerator.spawnBubble(playerText, sender, showers));
    }

    public void spawnBubble(String playerText, Channel channel, Player sender, Set<Player> showers) {
        if (!getStatusEnabledPredicate().test(sender)) return;
        bubbleGeneratorManager
                .getBubbleGenerator(sender, channel)
                .ifPresentOrElse(
                        bubbleGenerator -> spawnBubble(playerText, bubbleGenerator, sender, showers),
                        ()->Logger.debug("no bubble model found for player '{0}' (UUID: '{1}' )", sender.getName(), sender.getUniqueId().toString()));
    }

    public void removeAllBubbles() {
        bubbles.values().forEach(BubbleSpawned::remove);
        bubbles.clear();
    }

    @AllArgsConstructor
    public static class VanishManagerSet {
        private final Set<VanishManager> vanishManagerSet;

        public Predicate<Player> invisibleForAll() {
            return (player) -> vanishManagerSet.stream().allMatch(vanishManager -> vanishManager.isInvisible(player));
        }

        public Predicate<Player> invisibleForAny() {
            return (player) -> vanishManagerSet.stream().anyMatch(vanishManager -> vanishManager.isInvisible(player));
        }

        public Predicate<Player> visibleForAll() {
            return (player) -> vanishManagerSet.stream().noneMatch(vanishManager -> vanishManager.isInvisible(player));
        }

        public Predicate<Player> invisibleForAll(List<VanishManager> vanishManagers, boolean isWhite) {
            if (isWhite)
                return (player) -> vanishManagerSet.stream().filter(vanishManagers::contains).allMatch(vanishManager -> vanishManager.isInvisible(player));
            else
                return (player) -> vanishManagerSet.stream().filter(vanishManager -> !vanishManagers.contains(vanishManager)).allMatch(vanishManager -> vanishManager.isInvisible(player));
        }

        public Predicate<Player> invisibleForAny(List<VanishManager> vanishManagers, boolean isWhite) {
            if (isWhite)
                return (player) -> vanishManagerSet.stream().filter(vanishManagers::contains).anyMatch(vanishManager -> vanishManager.isInvisible(player));
            else
                return (player) -> vanishManagerSet.stream().filter(vanishManager -> !vanishManagers.contains(vanishManager)).anyMatch(vanishManager -> vanishManager.isInvisible(player));
        }

        public Predicate<Player> visibleForAll(List<VanishManager> vanishManagers, boolean isWhite) {
            if (isWhite)
                return (player) -> vanishManagerSet.stream().filter(vanishManagers::contains).noneMatch(vanishManager -> vanishManager.isInvisible(player));
            else
                return (player) -> vanishManagerSet.stream().filter(vanishManager -> !vanishManagers.contains(vanishManager)).noneMatch(vanishManager -> vanishManager.isInvisible(player));
        }
    }
}
