package thedivazo.messageoverhead.config;

import thedivazo.messageoverhead.api.logging.Logger;
import lombok.NoArgsConstructor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import thedivazo.messageoverhead.bubble.BubbleGenerator;
import thedivazo.messageoverhead.bubble.BubbleGeneratorManager;
import thedivazo.messageoverhead.bubble.BubbleManager;
import thedivazo.messageoverhead.config.channel.ChannelFactory;
import thedivazo.messageoverhead.utils.text.DecoratedString;
import thedivazo.messageoverhead.utils.ConfigWrapper;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ConfigManager {
    private boolean experimental_1_20 = false;

    LinkedHashSet<BubbleModel> bubbleModelSetInConfig = new LinkedHashSet<>();

    BubbleManager bubbleManagerInConfig;

    public Set<BubbleModel> getBubbleModelSet() {
        return Collections.unmodifiableSet(bubbleModelSetInConfig);
    }

    private ConfigWrapper currentConfig;

    public void loadConfig(ConfigWrapper currentConfig) throws InvalidConfigurationException {
        Logger.info("Start config loading...");
        this.currentConfig = currentConfig;
        loadBubbleModel();
        loadSettings();
        Logger.info("Config loaded");
    }

    private void loadBubbleModel() throws InvalidConfigurationException {
        ConfigWrapper bubbleModels = currentConfig.getRequiredConfigurationSection("models");
        bubbleModelSetInConfig.clear();
        Logger.info("Start models loading...");
        for (String bubbleModelName : bubbleModels.getKeys(false)) {
            Logger.info("Model '"+bubbleModelName+"' loading...");

            if(bubbleModelSetInConfig.stream().anyMatch(bubbleModel -> bubbleModel.getName().equals(bubbleModelName))) throw new InvalidConfigurationException("The model '"+bubbleModelName+"' has duplicate in config!");

            ConfigWrapper bubbleModel = bubbleModels.getRequiredConfigurationSection(bubbleModelName);
            BubbleModel.BubbleModelBuilder bubbleModelBuilder = BubbleModel.builder();

            BubbleModel.ParticleModel.ParticleModelBuilder particleModelBuilder = BubbleModel.ParticleModel.builder();
            BubbleModel.SoundModel.SoundModelBuilder soundModelBuilder = BubbleModel.SoundModel.builder();
            BubbleModel.LifeTimeModel.LifeTimeModelBuilder lifeTimeModelBuilder = BubbleModel.LifeTimeModel.builder();

            bubbleModelBuilder
                    .name(bubbleModelName)
                    .permission(bubbleModel.getString("permission", null))
                    .distance(bubbleModel.getDouble("distance", 10d))
                    .biasY(bubbleModel.getDouble("bias-y", 2.15d))
                    .visibleTextForOwner(bubbleModel.getBoolean("visible-text-for-owner", true))
                    .maxSizeLine(bubbleModel.getInt("max-size-line", 24));
            if (bubbleModel.isString("channel") || bubbleModel.isList("channel"))
                bubbleModel.getWrappedStringInListOrGetStringList("channel").forEach(channelName->bubbleModelBuilder.channel(ChannelFactory.create(channelName)));
            else bubbleModelBuilder.channel(ChannelFactory.create("all"));
            if(bubbleModel.isConfigurationSection("format")) {
                ConfigWrapper formatMessageModels = bubbleModel.getRequiredConfigurationSection("format");
                for (String formatMessageModelName : formatMessageModels.getKeys(false)) {
                    ConfigWrapper formatMessageModel = formatMessageModels.getRequiredConfigurationSection(formatMessageModelName);
                    BubbleModel.FormatMessageModel.FormatMessageModelBuilder formatMessageModelBuilder = BubbleModel.FormatMessageModel.builder();
                    formatMessageModelBuilder
                            .lines(formatMessageModel.getWrappedStringInListOrGetStringList("format").stream().map(DecoratedString::valueOf).collect(Collectors.toList()))
                            .permission(formatMessageModel.getString("permission", null));
                    bubbleModelBuilder.formatMessageModel(formatMessageModelBuilder.build());
                }
            }
            else bubbleModelBuilder.formatMessageModel(BubbleModel.FormatMessageModel.builder()
                    .lines(bubbleModel.getWrappedStringInListOrGetStringList("format").stream().map(DecoratedString::valueOf).collect(Collectors.toList()))
                    .permission(bubbleModel.getString("permission", null))
                    .build()
            );
            particleModelBuilder
                    .enable(bubbleModel.getBoolean("particle.enable", true))
                    .particle(Particle.valueOf(
                                    bubbleModel.getString("particle.type", Particle.CLOUD.name())))
                    .count(bubbleModels.getInt("particle.count", 4))
                    .offsetX(bubbleModel.getDouble("particle.offset-x", 0.2d))
                    .offsetY(bubbleModel.getDouble("particle.offset-y", 0.2d))
                    .offsetZ(bubbleModel.getDouble("particle.offset-z", 0.2d));
            soundModelBuilder
                    .enable(bubbleModel.getBoolean("sound.enable", true))
                    .sound(Sound.valueOf(bubbleModel.getString("sound.type", Sound.BLOCK_ANVIL_STEP.name())))
                    .volume(bubbleModel.getInt("sound.volume", 3))
                    .pitch(bubbleModel.getInt("sound.pitch", 3));
            lifeTimeModelBuilder
                    .timePerChar(bubbleModel.getDouble("time-per-char", 2))
                    .minTime(bubbleModel.getDouble("min-time", 3))
                    .maxTime(bubbleModel.getDouble("max-time", 20));
            bubbleModelBuilder
                    .particleModel(particleModelBuilder.build())
                    .soundModel(soundModelBuilder.build())
                    .lifeTimeModel(lifeTimeModelBuilder.build());
            bubbleModelSetInConfig.add(bubbleModelBuilder.build());

            Logger.info("Model '"+bubbleModelName+"' loaded");
        }
        Logger.info("Models loaded");
        bubbleManagerInConfig = generateBubbleManager();
        Logger.info("BubbleManager generated");
    }

    private void loadSettings() throws InvalidConfigurationException {
        Logger.info("Settings loading...");
        ConfigWrapper settings = currentConfig.getRequiredConfigurationSection("settings");
        experimental_1_20 = settings.getBoolean("1_20_experimental", false);
        Logger.info("Setting loaded");
    }

    private BubbleManager generateBubbleManager() {
        Set<BubbleGenerator> bubbleGeneratorSet = getBubbleModelSet().stream().map(BubbleGenerator::new).collect(Collectors.toSet());
        return new BubbleManager(new BubbleGeneratorManager(bubbleGeneratorSet));
    }

    public BubbleManager getBubbleManager() {
        return bubbleManagerInConfig;
    }

}
