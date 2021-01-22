package com.heretere.hch.core.internal.map;

import java.util.Map;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigMap extends TreeMap<@NotNull String, @Nullable Object> {
    public ConfigMap() {
        super();
    }

    public ConfigMap(final @NotNull Map<?, ?> original) {
        super();
        original.forEach((key, value) -> this.put(key.toString(), value));
    }
}
