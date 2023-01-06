package thedivazo.config;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImmutableConfigBubble extends ConfigBubble {

    public ImmutableConfigBubble(ConfigurationSection configBubbleSettings) {
        super.setParams(configBubbleSettings);
        super.saveFormatFromConfig(configBubbleSettings);
    }


    public ImmutableConfigBubble() {
        super();
    }

    @Override
    public void setParams(ConfigurationSection configBubbleSettings) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setParticleEnable(boolean isParticleEnable) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setParticleType(Particle particleType) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setParticleCount(int particleCount) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setParticleOffsetX(double particleOffsetX) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setParticleOffsetY(double particleOffsetY) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setParticleOffsetZ(double particleOffsetZ) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setSoundEnable(boolean isSoundEnable) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setSoundType(Sound soundType) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setSoundVolume(int soundVolume) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setSoundPitch(int soundPitch) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setDistance(int distance) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setBiasY(double biasY) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setVisibleTextForOwner(boolean isVisibleTextForOwner) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setPermSend(String permSend) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setPermSee(String permSee) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setDelay(int delay) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public void setSizeLine(int sizeLine) {
        throw new UnsupportedOperationException("You cannot access the parameter.");
    }

    @Override
    public Map<Integer, Format> getMessageFormat() {
        return Collections.unmodifiableMap(super.getMessageFormat());
    }

    @Override
    public List<String> getFormatOfPlayer(Player player) {
        return Collections.unmodifiableList(super.getFormatOfPlayer(player));
    }
}
