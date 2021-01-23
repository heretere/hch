package com.heretere.hch.core.backend.config;

import com.heretere.hch.core.backend.map.ConfigMap;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface ConfigWriter extends ConfigAdapter {
    boolean write(@NotNull Path fileLocation, @NotNull ConfigMap configMap);
}
