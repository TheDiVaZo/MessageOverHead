package me.thedivazo.messageoverhead.common.algoritm;

import lombok.AllArgsConstructor;
import me.thedivazo.messageoverhead.common.bubble.Bubble;
import me.thedivazo.messageoverhead.util.IterableUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author TheDiVaZo
 * created on 06.10.2024
 */
@AllArgsConstructor
public class SpectatorFinderImpl<S, B extends Bubble<?, ?>> implements SpectatorFinder<S, B> {
    private final Function<B, Iterable<S>> spectatorFinderFunction;
    private final Predicate<S> spectatorPredicate;

    @Override
    public @NonNull Set<S> findSpectators(B bubble) {
        return IterableUtil.toStream(spectatorFinderFunction.apply(bubble)).filter(spectatorPredicate).collect(Collectors.toSet());
    }
}
