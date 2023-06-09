package thedivazo.manager.config;

import io.th0rgal.oraxen.shaded.jetbrains.annotations.Unmodifiable;
import lombok.*;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

@EqualsAndHashCode
@Getter
@Builder(access = AccessLevel.PUBLIC)
public class BubbleModel {
    private final String name;
    private final ParticleModel particleModel;
    private final SoundModel soundModel;
    private final LifeTimeModel lifeTimeModel;
    @Singular
    @Unmodifiable
    private final List<FormatMessageModel> formatMessageModels;
    private final double distance;
    private final double biasY;
    private final boolean visibleTextForOwner;
    private final int maxSizeLine;
    private final String permission;


    @EqualsAndHashCode
    @Getter
    @Builder(access = AccessLevel.PUBLIC)
    public static class ParticleModel {
        private final boolean enable;
        private final Particle particle;
        private final int count;
        private final double offsetX;
        private final double offsetY;
        private final double offsetZ;
    }

    @EqualsAndHashCode
    @Getter
    @Builder(access = AccessLevel.PUBLIC)
    public static class SoundModel {
        private final boolean enable;
        private final Sound sound;
        private final int volume;
        private final int pitch;
    }

    @EqualsAndHashCode
    @Getter
    @Builder(access = AccessLevel.PUBLIC)
    public static class FormatMessageModel {
        private final String permission;
        @Singular
        @Unmodifiable
        private final List<String> lines;
    }

    @EqualsAndHashCode
    @Getter
    @Builder(access = AccessLevel.PUBLIC)
    public static class LifeTimeModel {
        private final double timePerChar;
        private final double minTime;
        private final double maxTime;
    }
}
