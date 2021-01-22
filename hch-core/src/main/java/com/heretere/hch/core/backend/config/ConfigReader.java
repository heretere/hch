package com.heretere.hch.core.backend.config;

import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.backend.map.ConfigMap;

public interface ConfigReader extends ConfigAdapter {
    @NotNull
    Optional<@NotNull ConfigMap> read(@NotNull Path fileLocation);
}
