package com.heretere.hch.core;

import com.google.gson.reflect.TypeToken;
import com.heretere.hch.core.backend.ErrorHolder;
import com.heretere.hch.core.backend.config.ConfigReader;
import com.heretere.hch.core.backend.config.ConfigWriter;
import com.heretere.hch.core.backend.map.ConfigMap;
import com.heretere.hch.core.backend.util.ConfigMapperUtils;
import com.heretere.hch.core.exception.InvalidPojoException;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class HCHConfig implements ErrorHolder {
    private final @NotNull MultiConfigHandler parent;
    private final @NotNull ConfigMap pojos;
    private final @NotNull Path fileLocation;
    private final @NotNull Set<@NotNull Throwable> errors;
    private @NotNull ConfigMap config;

    public HCHConfig(
            final @NotNull MultiConfigHandler parent,
            final @NotNull Path fileLocation
    ) {
        this.parent = parent;
        this.config = new ConfigMap();
        this.pojos = new ConfigMap();
        this.fileLocation = fileLocation;
        this.errors = new HashSet<>();
    }

    public boolean createIfNotExists() {
        if (this.errors.isEmpty()) {
            try {
                if (!Files.exists(this.fileLocation)) {
                    Files.createDirectories(this.fileLocation.getParent());
                    Files.createFile(this.fileLocation);
                }
            } catch (Exception e) {
                this.errors.add(e);
            }
        }

        return this.errors.isEmpty();
    }

    public @NotNull Optional<@NotNull Object> get(final @NotNull String dottedKey) {
        return Optional.ofNullable(dottedKey.isEmpty() ? this.config : this.config.get(dottedKey));
    }

    public boolean read(final @NotNull ConfigReader reader) {
        if (this.errors.isEmpty() && this.createIfNotExists()) {
            final Optional<ConfigMap> configMap = reader.read(this.fileLocation);

            if (configMap.isPresent()) {
                this.config = ConfigMapperUtils.inflateMap(configMap.get());
            } else {
                this.errors.addAll(reader.getErrors());
            }
        }

        return this.errors.isEmpty();
    }

    private void recursiveUpdatePOJOParent(
            final @NotNull String key,
            final @NotNull ConfigMap value
    ) {
        final String parentKey = ConfigMapperUtils.getEverythingBeforeLastPeriod(key);
        final String baseKey = ConfigMapperUtils.getEverythingAfterLastPeriod(key);

        if (baseKey == null) {
            return;
        }

        final ConfigMap attach = new ConfigMap();
        attach.put(baseKey, value);

        if (parentKey != null) {
            this.config.put(parentKey, ConfigMapperUtils.deepMerge((ConfigMap) this.config.get(parentKey), attach));
            this.recursiveUpdatePOJOParent(parentKey, attach);
        }
    }

    private boolean updateConfigWithPOJOs() {
        try {
            this.pojos.forEach((key, pojo) -> {
                final ConfigMap newPOJOSection = this.parent.getGsonBackend()
                        .fromJson(
                                this.parent.getGsonBackend().toJson(pojo),
                                new TypeToken<ConfigMap>() {}.getType()
                        );

                this.config.put(key, ConfigMapperUtils.deepMerge((ConfigMap) this.config.get(key), newPOJOSection));
                newPOJOSection.forEach((childKey, value) -> this.config.put(key + "." + childKey, value));

                this.recursiveUpdatePOJOParent(key, newPOJOSection);
            });
        } catch (Exception e) {
            this.errors.add(e);
        }

        return this.errors.isEmpty();
    }

    public boolean write(final @NotNull ConfigWriter writer) {
        if (
                this.errors.isEmpty()
                        && this.createIfNotExists()
                        && this.updateConfigWithPOJOs()
                        && !writer.write(this.fileLocation, ConfigMapperUtils.deflateMap(this.config))
        ) {
            this.errors.addAll(writer.getErrors());
        }

        return this.errors.isEmpty();
    }

    public void registerPOJOAtPath(
            final @NotNull String key,
            final @NotNull Object pojo
    ) {
        if (this.pojos.containsKey(key)) {
            throw new InvalidPojoException(
                    String.format(
                            "Pojo already registered at path '%s'.",
                            key
                    )
            );
        }

        this.pojos.put(key, pojo);
    }

    public @NotNull Path getFileLocation() {
        return this.fileLocation;
    }

    @Override
    public @NotNull Set<@NotNull Throwable> getErrors() {
        return Collections.unmodifiableSet(this.errors);
    }
}
