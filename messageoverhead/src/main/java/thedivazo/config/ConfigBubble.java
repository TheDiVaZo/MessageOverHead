package thedivazo.config;

import api.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import thedivazo.MessageOverHear;
import thedivazo.utils.ConfigUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigBubble {
    @Getter
    @Setter
    private boolean isParticleEnable = true;
    @Getter
    @Setter
    private Particle particleType = Particle.CLOUD;
    @Getter
    @Setter
    private int particleCount = 4;
    @Getter
    @Setter
    private double particleOffsetX = 0.2;
    @Getter
    @Setter
    private double particleOffsetY = 0.2;
    @Getter
    @Setter
    private double particleOffsetZ = 0.2;

    @Getter
    @Setter
    private boolean isSoundEnable = true;
    @Getter
    @Setter
    private Sound soundType = Sound.BLOCK_ANVIL_STEP;
    @Getter
    @Setter
    private int soundVolume = 4;
    @Getter
    @Setter
    private int soundPitch = 4;

    public static final String DEFAULT_MESSAGE_FORMAT = "%player_name% %message%";

    @Getter
    private final Map<Integer, Format> messageFormat = new LinkedHashMap<>();

    @Getter
    @Setter
    private int distance = 10;
    @Getter
    @Setter
    private double biasY = 2.15;

    @Getter
    @Setter
    private boolean isVisibleTextForOwner = true;

    @Getter
    private String permSend = "moh.send";
    @Getter
    private String permSee = "moh.see";

    @Getter
    @Setter
    private int delay = 4;
    @Getter
    @Setter
    private int sizeLine = 24;

    public void setParams(ConfigurationSection configBubbleSettings) {
        isSoundEnable = (configBubbleSettings.getBoolean("messages.sound.enable", isSoundEnable()));
        isParticleEnable = (configBubbleSettings.getBoolean("particle.enable", isParticleEnable()));
        if (isParticleEnable()) {
            try {
                particleType = (Particle.valueOf(configBubbleSettings.getString("particle.particleType",getParticleType().name())));
            } catch (IllegalArgumentException e) {
                Logger.warn("Wrong particle type. Please check your config. Default particle type set: CLOUD");
            }
            particleCount = (configBubbleSettings.getInt("particle.count", particleCount));
            particleOffsetX = (configBubbleSettings.getDouble("particle.offsetX", particleOffsetX));
            particleOffsetY = (configBubbleSettings.getDouble("particle.offsetY", particleOffsetY));
            particleOffsetZ = (configBubbleSettings.getDouble("particle.offsetZ", particleOffsetZ));
        }
        if (isSoundEnable()) {
            try {
                soundType = (Sound.valueOf(configBubbleSettings.getString("sound.soundType", getSoundType().name())));
            } catch (IllegalArgumentException e) {
                Logger.warn("Wrong sound type. Please check your config "+configBubbleSettings.getName()+". Default sound type set: BLOCK_ANVIL_STEP");
            }
            soundVolume = (configBubbleSettings.getInt("sound.volume", soundVolume));
            soundPitch = (configBubbleSettings.getInt("sound.pitch", soundPitch));
        }
        distance = (configBubbleSettings.getInt("settings.distance", distance));
        biasY = (configBubbleSettings.getDouble("settings.biasY", biasY));
        isVisibleTextForOwner = (configBubbleSettings.getBoolean("settings.visibleTextForOwner", isVisibleTextForOwner));
        delay = (configBubbleSettings.getInt("settings.delay", delay));
        sizeLine = (configBubbleSettings.getInt("settings.sizeLine", sizeLine));
    }

    void saveFormatFromConfig(ConfigurationSection configBubbleSettings) {
        ConfigUtils configBubbleSettingsWrapper = new ConfigUtils(configBubbleSettings);
        messageFormat.clear();
        ConfigurationSection permissionsFormat = configBubbleSettings.getConfigurationSection("settings.format");

        if (permissionsFormat == null) {
            List<String> formatsMessage = configBubbleSettingsWrapper.getAlwaysListString("settings.format");
            if(!formatsMessage.isEmpty()) messageFormat.put(0, new Format(formatsMessage, null));
        } else {
            for (String priority : permissionsFormat.getKeys(false)) {
                ConfigurationSection sectionFormat = permissionsFormat.getConfigurationSection(priority);
                if (sectionFormat != null) {
                    List<String> formatsMessage = configBubbleSettingsWrapper.getAlwaysListString("format", sectionFormat).stream().filter(Objects::nonNull).collect(Collectors.toList());
                    if (!formatsMessage.isEmpty()) {
                        String perm = sectionFormat.getString("perm");
                        messageFormat.put(Integer.parseInt(priority), new ConfigBubble.Format(formatsMessage, perm));
                    }
                }
            }
        }
    }

    public ConfigBubble(){}

    public ConfigBubble(ConfigurationSection configBubbleSettings) {
        this.setParams(configBubbleSettings);
        this.saveFormatFromConfig(configBubbleSettings);
    }

    public static class Format {
        private final String permission;
        private final ArrayList<String> formatsMessage = new ArrayList<>();

        public List<String> getFormatsMessage() {
            return formatsMessage;
        }

        public String getPermission() {
            return permission;
        }

        public Format(String formatMessage, String permission) {
            this.formatsMessage.add(formatMessage);
            this.permission = permission;
        }
        public Format(List<String> formatsMessage, String permission) {
            this.formatsMessage.addAll(formatsMessage);
            this.permission = permission;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Format)) return false;
            Format format = (Format) o;
            return Objects.equals(getFormatsMessage(), format.getFormatsMessage()) && Objects.equals(getPermission(), format.getPermission());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getFormatsMessage(), getPermission());
        }
    }


    public List<String> getFormatOfPlayer(Player player) {
        List<String> defaultFormat = new ArrayList<>();
        defaultFormat.add(DEFAULT_MESSAGE_FORMAT);

        for (Map.Entry<Integer, Format> priorityAndFormat : getMessageFormat().entrySet()) {
            String perm = priorityAndFormat.getValue().getPermission();
            List<String> format = priorityAndFormat.getValue().getFormatsMessage();
            if (perm == null || player.hasPermission(perm)) {
                defaultFormat = format;
            }
        }
        return defaultFormat;
    }

    public boolean haveSendPermission(Player player) {
        if(MessageOverHear.getConfigManager().isVault()) {
            return MessageOverHear.getConfigManager().getPermissionVault().has(player, getPermSend());
        }
        else return player.hasPermission(getPermSend());
    }
    public boolean haveSeePermission(Player player) {
        if(MessageOverHear.getConfigManager().isVault()) {
            return MessageOverHear.getConfigManager().getPermissionVault().has(player, getPermSee());
        }
        else return player.hasPermission(getPermSee());
    }
}
