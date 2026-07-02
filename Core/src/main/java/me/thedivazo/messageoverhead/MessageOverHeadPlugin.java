package me.thedivazo.messageoverhead;

import me.thedivazo.messageoverhead.animation.AnimationUpHandler;
import me.thedivazo.messageoverhead.api.ComponentService;
import me.thedivazo.messageoverhead.api.ProfileService;
import me.thedivazo.messageoverhead.api.SpawnService;
import me.thedivazo.messageoverhead.armorstand.ArmorStandBubbleFactory;
import me.thedivazo.messageoverhead.command.BubbleTestCommand;
import me.thedivazo.messageoverhead.core.BubbleContainer;
import me.thedivazo.messageoverhead.core.DefaultBubbleFactory;
import me.thedivazo.messageoverhead.core.OnlinePlayerProvider;
import me.thedivazo.messageoverhead.core.component.*;
import me.thedivazo.messageoverhead.core.component.scope.OffsetComponentScoped;
import me.thedivazo.messageoverhead.core.component.scope.ScopedFactory;
import me.thedivazo.messageoverhead.core.event.EventBus;
import me.thedivazo.messageoverhead.core.event.SimpleEventBus;
import me.thedivazo.messageoverhead.core.event.SpawnBubbleEvent;
import me.thedivazo.messageoverhead.core.tick.BubbleScheduler;
import me.thedivazo.messageoverhead.core.tick.BukkitBubbleScheduler;
import me.thedivazo.messageoverhead.profile.BubbleProfile;
import me.thedivazo.messageoverhead.profile.ProfileId;
import me.thedivazo.messageoverhead.profile.ProfileRegistry;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import me.thedivazo.messageoverhead.util.Position;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageOverHeadPlugin extends JavaPlugin {
    public static final OnlinePlayerProvider DEFAULT_ONLINE_PLAYER_PROVIDER = new OnlinePlayerProvider();
    private static final ProfileId DEFAULT_PROFILE_ID = ProfileId.of(ProfileId.DEFAULT_NAMESPACE, "default");
    private static MessageOverHeadPlugin INSTANCE;

    public static MessageOverHeadPlugin getInstance() {
        return INSTANCE;
    }

    public static final MinecraftVersion SERVER_VERSION = MinecraftVersion.parse(Bukkit.getMinecraftVersion());

    private BubbleScheduler bubbleScheduler;
    private BukkitTask bubbleContainerInvalidationTask;

    // Services
    private ComponentService componentService;
    private ProfileService profileService;
    private SpawnService spawnService;
    private LegacyPaperCommandManager<CommandSender> commandManager;

    private final EventBus eventBus = new SimpleEventBus();

    @Override
    public void onEnable() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized!");
        }
        INSTANCE = this;

        BubbleContainer bubbleContainer = new BubbleContainer();
        ComponentRegistry componentRegistry = new ComponentRegistry();
        ProfileRegistry profileRegistry = new ProfileRegistry();
        ProfileComponentIndex profileIndex = new ProfileComponentIndex();
        bubbleScheduler = new BukkitBubbleScheduler(this);

        this.componentService = new ComponentService(componentRegistry, bubbleContainer);
        this.profileService = new ProfileService(profileRegistry, componentRegistry, profileIndex);
        this.spawnService = new SpawnService(bubbleScheduler, bubbleContainer, profileIndex, profileRegistry, eventBus);

        registerDefaultProfile(componentRegistry);

        commandManager = LegacyPaperCommandManager.createNative(
                this,
                ExecutionCoordinator.simpleCoordinator()
        );
        BubbleTestCommand.register(commandManager, spawnService, DEFAULT_PROFILE_ID);

        eventBus.subscribe(SpawnBubbleEvent.class, new AnimationUpHandler(spawnService));

        bubbleContainerInvalidationTask = Bukkit.getScheduler().runTaskTimer(
                this,
                bubbleContainer::invalidate,
                200L,
                200L
        );
    }

    private void registerDefaultProfile(ComponentRegistry componentRegistry) {
        Map<ComponentKey<?>, BubbleComponentFactory<?>> componentFactories = new LinkedHashMap<>();
        Map<String, ScopedFactory<Position>> positionScopedFactories = new LinkedHashMap<>();
        positionScopedFactories.put(
                OffsetComponentScoped.DEFAULT_SCOPED_ID,
                OffsetComponentScoped.factory(0, 2.5, 0)
        );

        componentFactories.put(
                PositionComponent.key(),
                PositionComponent.factory(positionScopedFactories)
        );
        componentFactories.put(
                ViewComponent.key(),
                ViewComponent.factory(
                        MessageOverHeadPlugin.DEFAULT_ONLINE_PLAYER_PROVIDER,
                        new ViewComponent.Settings(20, 5)
                )
        );
        componentFactories.put(
                LifetimeComponent.key(),
                LifetimeComponent.factory(120)
        );

        BubbleProfile defaultProfile = BubbleProfile.create(
                DEFAULT_PROFILE_ID,
                new DefaultBubbleFactory(componentRegistry, new ArmorStandBubbleFactory(0.25), eventBus),
                componentFactories
        );
        profileService.register(defaultProfile);
    }

    @Override
    public void onDisable() {
        try {
            if (bubbleContainerInvalidationTask != null) {
                bubbleContainerInvalidationTask.cancel();
            }
            if (spawnService != null) {
                spawnService.clear();
            }
        } finally {
            bubbleContainerInvalidationTask = null;
            bubbleScheduler = null;
            commandManager = null;
            spawnService = null;
            profileService = null;
            INSTANCE = null;
        }
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public ProfileService getProfileService() {
        return profileService;
    }

    public SpawnService getSpawnService() {
        return spawnService;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
