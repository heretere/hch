package com.heretere.hch.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class ConfigSet<T> implements ConfigCollection<T> {
    private final @NotNull Class<T> type;
    private final @NotNull Set<@NotNull T> backingSet;

    private ConfigSet(
        final @NotNull Class<T> type,
        final @NotNull Set<@NotNull T> backingSet
    ) {
        this.type = type;
        this.backingSet = backingSet;
    }

    /**
     * @param type        The class of the generic type.
     * @param backendList The backend list implementation.
     * @param <T>         The generic type of the list.
     * @return A new config list instance
     */
    @SuppressWarnings("unchecked")
    public static <T> ConfigSet<T> newInstance(
        final @NotNull Class<T> type,
        final @NotNull Collection<?> backendList
    ) {
        return new ConfigSet<>(type, (Set<T>) backendList);
    }

    @Override public void add(T type) {
        this.backingSet.add(type);
    }

    @Override public void remove(T type) {
        this.backingSet.remove(type);
    }

    @Override public @NotNull Collection<@NotNull T> getBackingCollection() {
        return this.backingSet;
    }

    @Override public @NotNull Class<T> getGenericType() {
        return this.type;
    }
}
