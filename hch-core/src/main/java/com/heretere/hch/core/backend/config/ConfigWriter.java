package com.heretere.hch.core.backend.config;

import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.backend.map.ConfigMap;

public interface ConfigWriter extends ConfigAdapter {
    boolean write(@NotNull Path fileLocation, @NotNull ConfigMap configMap);
}
