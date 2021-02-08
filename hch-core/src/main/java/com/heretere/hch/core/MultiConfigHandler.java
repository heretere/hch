package com.heretere.hch.core;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heretere.hch.core.annotation.ConfigFile;
import com.heretere.hch.core.annotation.POJOKey;
import com.heretere.hch.core.backend.ErrorHolder;
import com.heretere.hch.core.backend.config.ConfigAdapter;
import com.heretere.hch.core.backend.config.ConfigReader;
import com.heretere.hch.core.backend.config.ConfigWriter;
import com.heretere.hch.core.backend.util.ConfigMapperUtils;
import com.heretere.hch.core.exception.InvalidFileExtensionException;
import com.heretere.hch.core.exception.InvalidPojoException;

public class MultiConfigHandler implements ErrorHolder {
    private final @NotNull Path basePath;
    private final @NotNull Map<@NotNull String, @NotNull ConfigReader> readers;
    private final @NotNull Map<@NotNull String, @NotNull ConfigWriter> writers;
    private final @NotNull Map<@NotNull Path, @NotNull HCHConfig> configs;
    private final @NotNull Set<@NotNull Throwable> errors;
    private final @NotNull GsonBuilder gsonBuilder;
    private @NotNull Gson gson;

    public MultiConfigHandler(final @NotNull Path basePath) {
        this.gsonBuilder =
            new GsonBuilder()
                .setPrettyPrinting();

        this.gson = this.gsonBuilder.create();

        this.basePath = basePath;

        this.readers = new HashMap<>();
        this.writers = new HashMap<>();

        this.configs = new HashMap<>();

        this.errors = new HashSet<>();
    }

    private static String getExtensionFromFileName(final @NotNull String name) {
        final String extension = ConfigMapperUtils.getEverythingAfterLastPeriod(name);

        if (extension == null || extension.equals(name)) {
            throw new InvalidFileExtensionException(
                    String.format(
                        "Couldn't find file extension in file name '%s'.",
                        name
                    )
            );
        }

        return extension;
    }

    public <T> @NotNull Optional<@NotNull T> loadPOJOClassAtPath(
            final @NotNull String relativePath,
            final @NotNull String pojoPath,
            final @NotNull Class<T> clazz
    ) {
        final HCHConfig config = this.getOrGenerateConfigByRelativePath(relativePath);

        try {
            final ConfigReader reader = this.getReaderByFileName(config.getFileLocation().getFileName().toString());

            if (!config.read(reader)) {
                this.errors.addAll(config.getErrors());
            }

            final AtomicReference<@NotNull String> path = new AtomicReference<>(pojoPath);

            Optional.ofNullable(clazz.getAnnotation(POJOKey.class))
                .ifPresent(annotation -> path.set(annotation.value()));

            final Object value = config.get(path.get())
                .orElseThrow(
                    () -> new InvalidPojoException(
                            String.format(
                                "No value found at pojo key '%s' for class '%s'.",
                                path,
                                clazz.getName()
                            )
                    )
                );

            final T pojo = this.gson.fromJson(this.gson.toJson(value), clazz);

            if (pojo != null) {
                config.registerPOJOAtPath(path.get(), pojo);
                return Optional.of(pojo);
            }
        } catch (Exception e) {
            this.errors.add(e);
        }

        return Optional.empty();
    }

    public <T> @NotNull Optional<@NotNull T> loadPOJOClass(final @NotNull Class<T> clazz) {
        if (!clazz.isAnnotationPresent(ConfigFile.class)) {
            throw new InvalidPojoException(
                    String.format(
                        "Class '%s' doesn't define a ConfigFile annotation. Please add one.",
                        clazz.getName()
                    )
            );
        }

        return this.loadPOJOClassAtPath(clazz.getAnnotation(ConfigFile.class).value(), "", clazz);
    }

    public boolean saveConfig(final @NotNull HCHConfig config, final boolean forcePOJOValues) {
        if (!this.errors.isEmpty()) {
            return false;
        }

        final String fileName = config.getFileLocation().getFileName().toString();

        if (
            !config.read(this.getReaderByFileName(fileName))
                || !config.write(this.getWriterByFileName(fileName), forcePOJOValues)
        ) {
            this.errors.addAll(config.getErrors());
            return false;
        }

        return true;
    }

    public boolean saveAllConfigs(final boolean forcePOJOValues) {
        if (this.errors.isEmpty()) {
            this.configs
                .values()
                .forEach(config -> this.saveConfig(config, forcePOJOValues));
        }

        return this.errors.isEmpty();
    }

    private HCHConfig getOrGenerateConfigByRelativePath(final @NotNull String relativePath) {
        final HCHConfig output;
        final Path path = this.basePath.resolve(relativePath);

        final Optional<HCHConfig> optionalConfig = this.getConfigByPath(path);

        if (optionalConfig.isPresent()) {
            output = optionalConfig.get();
        } else {
            output = new HCHConfig(this, path);
            this.configs.put(path, output);
        }

        return output;
    }

    private @NotNull ConfigReader getReaderByFileName(final @NotNull String name) {
        final String extension = MultiConfigHandler.getExtensionFromFileName(name);
        return Optional.ofNullable(this.readers.get(extension))
            .orElseThrow(
                () -> new InvalidFileExtensionException(
                        String.format(
                            "Couldn't find registered Config Reader for file '%s'."
                                + "File extension found: '%s'.",
                            name,
                            extension
                        )
                )
            );
    }

    private @NotNull ConfigWriter getWriterByFileName(final @NotNull String name) {
        final String extension = MultiConfigHandler.getExtensionFromFileName(name);
        return Optional.ofNullable(this.writers.get(extension))
            .orElseThrow(
                () -> new InvalidFileExtensionException(
                        String.format(
                            "Couldn't find registered Config Writer for file '%s'."
                                + "File extension found: '%s'.",
                            name,
                            extension
                        )
                )
            );
    }

    public Optional<HCHConfig> getConfigByRelativePath(final @NotNull String path) {
        return this.getConfigByPath(this.basePath.resolve(path));
    }

    public Optional<HCHConfig> getConfigByPath(final @NotNull Path path) {
        return Optional.ofNullable(this.configs.get(path));
    }

    public MultiConfigHandler registerFileExtensionHandler(
            final @NotNull ConfigAdapter adapter,
            final @NotNull String @NotNull... extensions
    ) {

        if (adapter instanceof ConfigReader) {
            final ConfigReader reader = (ConfigReader) adapter;

            for (String extension : extensions) {
                this.readers.put(extension, reader);
            }
        }

        if (adapter instanceof ConfigWriter) {
            final ConfigWriter writer = (ConfigWriter) adapter;

            for (String extension : extensions) {
                this.writers.put(extension, writer);
            }
        }

        return this;
    }

    public MultiConfigHandler registerTypeAdapter(final @NotNull Class<?> type, final @NotNull Object adapter) {
        this.gsonBuilder.registerTypeAdapter(type, adapter);
        this.gson = this.gsonBuilder.create();
        return this;
    }

    public MultiConfigHandler registerTypeAdapters(final @NotNull Map<@NotNull Class<?>, @NotNull Object> adapters) {
        adapters.forEach(this.gsonBuilder::registerTypeAdapter);
        this.gson = this.gsonBuilder.create();
        return this;
    }

    public @NotNull Gson getGsonBackend() {
        return this.gson;
    }

    @Override
    public @NotNull Set<@NotNull Throwable> getErrors() {
        return Collections.unmodifiableSet(this.errors);
    }
}
