package thedivazo.messageoverhead.config;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import thedivazo.messageoverhead.bubble.BubbleModel;
import thedivazo.messageoverhead.channel.ChannelFactory;
import thedivazo.messageoverhead.logging.Logger;
import thedivazo.messageoverhead.util.ConfigSectionDecorator;
import thedivazo.messageoverhead.util.text.DecoratedString;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigAdapter {
    private ConfigSectionDecorator configuration;

    @Getter
    private LinkedHashSet<BubbleModel> bubbleModels = new LinkedHashSet<>();

    @Getter
    private Setting settings;

    @Getter
    @Builder
    public static class Setting {
        private boolean isExperemental_1_20;
        private boolean isDebugMode;
        private String enableCommandMessage;
        private String disableCommandMessage;
        private String sendCommandMessage;
        private String createCommandMessage;
    }

    public ConfigAdapter(FileConfiguration configuration) throws InvalidConfigurationException {
        updateConfiguration(configuration);
    }

    public void updateConfiguration(FileConfiguration fileConfiguration) throws InvalidConfigurationException {
        this.configuration = new ConfigSectionDecorator(fileConfiguration);
        updateBubbleModels();
        updateSettings();
    }

    private void updateBubbleModels() throws InvalidConfigurationException {
        ConfigSectionDecorator configBubbleModels = configuration.getRequiredConfigurationSection("models");
        bubbleModels.clear();
        Logger.info("Start models loading...");
        for (String bubbleModelName : configBubbleModels.getKeys(false)) {
            Logger.info("Model '" + bubbleModelName + "' loading...");

            if (bubbleModels.stream().anyMatch(bubbleModel -> bubbleModel.getName().equals(bubbleModelName)))
                throw new InvalidConfigurationException("The model '" + bubbleModelName + "' has duplicate in config!");

            ConfigSectionDecorator bubbleModel = configBubbleModels.getRequiredConfigurationSection(bubbleModelName);
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
                bubbleModel.getWrappedStringInListOrGetStringList("channel", List.of("{message}"))
                        .forEach(channelName -> bubbleModelBuilder.channel(ChannelFactory.create(channelName)));
            else bubbleModelBuilder.channel(ChannelFactory.create("all"));
            if (bubbleModel.isConfigurationSection("settings.format")) {
                ConfigSectionDecorator formatMessageModels = bubbleModel.getRequiredConfigurationSection("settings.format");
                for (String formatMessageModelName : formatMessageModels.getKeys(false)) {
                    ConfigSectionDecorator formatMessageModel = formatMessageModels.getRequiredConfigurationSection(formatMessageModelName);
                    BubbleModel.FormatMessageModel.FormatMessageModelBuilder formatMessageModelBuilder = BubbleModel.FormatMessageModel.builder();
                    formatMessageModelBuilder
                            .lines(formatMessageModel.getWrappedStringInListOrGetStringList("format", List.of("{message}")).stream()
                                    .map(DecoratedString::valueOf).collect(Collectors.toList()))
                            .permission(formatMessageModel.getString("permission", null));
                    bubbleModelBuilder.formatMessageModel(formatMessageModelBuilder.build());
                }
            } else bubbleModelBuilder.formatMessageModel(BubbleModel.FormatMessageModel.builder()
                    .lines(bubbleModel.getWrappedStringInListOrGetStringList("settings.format", List.of("{message}")).stream()
                            .map(DecoratedString::valueOf).collect(Collectors.toList()))
                    .permission(null)
                    .build()
            );
            particleModelBuilder
                    .enable(bubbleModel.getBoolean("particle.enable", true))
                    .particle(Particle.valueOf(
                            bubbleModel.getString("particle.type", Particle.CLOUD.name())))
                    .count(configBubbleModels.getInt("particle.count", 4))
                    .offsetX(bubbleModel.getDouble("particle.offset-x", 0.2d))
                    .offsetY(bubbleModel.getDouble("particle.offset-y", 0.2d))
                    .offsetZ(bubbleModel.getDouble("particle.offset-z", 0.2d));
            soundModelBuilder
                    .enable(bubbleModel.getBoolean("sound.enable", true))
                    .sound(Sound.valueOf(bubbleModel.getString("sound.type", Sound.BLOCK_ANVIL_STEP.name())))
                    .volume(bubbleModel.getInt("sound.volume", 3))
                    .pitch(bubbleModel.getInt("sound.pitch", 3));
            lifeTimeModelBuilder
                    .timePerChar(bubbleModel.getDouble("lifetime.time-per-char", 2))
                    .minTime(bubbleModel.getDouble("lifetime.min-time", 3))
                    .maxTime(bubbleModel.getDouble("lifetime.max-time", 20));
            bubbleModelBuilder
                    .particleModel(particleModelBuilder.build())
                    .soundModel(soundModelBuilder.build())
                    .lifeTimeModel(lifeTimeModelBuilder.build());
            bubbleModels.add(bubbleModelBuilder.build());

            Logger.info("Model '" + bubbleModelName + "' loaded");
        }
        Logger.info("Models loaded");
    }

    private void updateSettings() throws InvalidConfigurationException {
        Logger.info("Settings loading...");
        ConfigSectionDecorator configSettings = configuration.getRequiredConfigurationSection("settings");
        ConfigSectionDecorator configCommandMessages = configSettings.getRequiredConfigurationSection("messages.command");
        settings = Setting.builder()
                .isExperemental_1_20(configSettings.getBoolean("1_20_experimental", false))
                .isDebugMode(configuration.getBoolean("debug", false))
                .enableCommandMessage(configCommandMessages.getString("enable", "bubble enabled"))
                .disableCommandMessage(configCommandMessages.getString("disable", "bubble disabled"))
                .sendCommandMessage(configCommandMessages.getString("send", "bubble send"))
                .createCommandMessage(configCommandMessages.getString("create", "bubble create"))
                .build();
        Logger.info("Setting loaded");
    }

}
