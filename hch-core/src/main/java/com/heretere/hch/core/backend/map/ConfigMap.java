package com.heretere.hch.core.backend.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

public class ConfigMap extends TreeMap<@NotNull String, @Nullable Object> {
    public ConfigMap() {
        super();
    }

    public static @NotNull ConfigMap copy(final @NotNull Map<?, ?> original) {
        final ConfigMap map = new ConfigMap();
        original.forEach((key, value) -> map.put(key.toString(), value));
        return map;
    }
}
