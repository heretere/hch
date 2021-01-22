package com.heretere.hch.core.internal.config;

import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.internal.map.ConfigMap;

public interface ConfigWriter extends ConfigAdapter {
    boolean write(@NotNull Path fileLocation, @NotNull ConfigMap configMap);
}
