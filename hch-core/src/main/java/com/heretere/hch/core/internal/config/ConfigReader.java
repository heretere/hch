package com.heretere.hch.core.internal.config;

import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.internal.map.ConfigMap;

public interface ConfigReader extends ConfigAdapter {
    @NotNull
    Optional<@NotNull ConfigMap> read(@NotNull Path fileLocation);
}
