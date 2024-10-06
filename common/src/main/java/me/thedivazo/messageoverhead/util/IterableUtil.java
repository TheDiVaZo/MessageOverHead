package me.thedivazo.messageoverhead.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author TheDiVaZo
 * created on 06.10.2024
 */
public final class IterableUtil {
    public static <R> Stream<R> toStream(Iterable<R> iterable, boolean parallel) {
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    public static <R> Stream<R> toStream(Iterable<R> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <R> Set<R> toSet(Iterable<R> iterable) {
        return toStream(iterable).collect(Collectors.toSet());
    }
}
