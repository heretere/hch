package com.heretere.hch;

import com.google.common.collect.Maps;
import com.heretere.hch.processor.Processor;
import com.heretere.hch.processor.exception.InvalidTypeException;
import com.heretere.hch.processor.toml.TomlProcessor;
import com.heretere.hch.processor.yaml.YamlProcessor;
import com.heretere.hch.structure.annotation.ConfigFile;
import com.heretere.hch.structure.backend.ConfigClassParser;
import com.heretere.hch.structure.backend.ConfigPath;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public class MultiConfigHandler {
    private final @NotNull Path basePath;
    private final @NotNull ConfigClassParser parser;
    private final @NotNull Map<String, Processor<?>> files;

    public MultiConfigHandler(
        final @NotNull Path basePath
    ) {
        this.basePath = basePath;
        this.parser = new ConfigClassParser();

        this.files = Maps.newHashMap();
    }


    private Processor<?> getProcessor(
        final @NotNull String path,
        final @NotNull ProcessorType processorType
    ) {
        return this.files.computeIfAbsent(path, key -> {
            switch (processorType) {
                case TOML:
                    return new TomlProcessor(this.basePath.resolve(key));
                case YAML:
                    return new YamlProcessor(this.basePath.resolve(key));
                default:
                    throw new IllegalArgumentException(String.format("Invalid Processor Type (%s).", processorType));
            }
        });
    }

    /**
     * Load config class.
     *
     * @param instance the instance
     */
    public void loadConfigClass(
        final @NotNull Object instance,
        final @NotNull ProcessorType processorType
    ) throws IllegalAccessException, InvalidTypeException {
        Map<String, ConfigPath> configPaths = this.parser.getConfigPaths(instance);

        if (!configPaths.isEmpty()) {
            ConfigFile file = instance.getClass().getAnnotation(ConfigFile.class);
            Processor<?> processor = this.getProcessor(file.value(), processorType);

            for (ConfigPath configPath : configPaths.values()) {
                processor.processConfigPath(configPath);
            }
        }

        this.load();
    }

    /**
     * Loads a collection of configs paths.
     *
     * @param relativeFilePath The relative path to the config file.
     * @param configPaths      The collection of config paths.
     */
    public void loadConfigPaths(
        final @NotNull String relativeFilePath,
        final @NotNull Collection<ConfigPath> configPaths,
        final @NotNull ProcessorType processorType
    ) throws IllegalAccessException, InvalidTypeException {
        Processor<?> processor = this.getProcessor(relativeFilePath, processorType);

        for (ConfigPath configPath : configPaths) {
            processor.processConfigPath(configPath);
        }

        this.load();
    }

    /**
     * Loads all the configs.
     *
     * @return true if all loaded successfully.
     */
    public boolean load() {
        for (Processor<?> processor : this.files.values()) {
            try {
                if (!processor.load()) {
                    return false;
                }
            } catch (IOException | InvalidTypeException | IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Saves all the loaded config files.
     *
     * @return true if all configs were saved successfully.
     */
    public boolean unload() {
        for (Processor<?> processor : this.files.values()) {
            try {
                if (!processor.save()) {
                    return false;
                }
            } catch (IllegalAccessException | IOException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets base path.
     *
     * @return the base path
     */
    public @NotNull Path getBasePath() {
        return this.basePath;
    }
}
