package com.heretere.hch.yaml;

import com.google.gson.reflect.TypeToken;
import com.heretere.hch.core.MultiConfigHandler;
import com.heretere.hch.core.backend.config.ConfigReader;
import com.heretere.hch.core.backend.config.ConfigWriter;
import com.heretere.hch.core.backend.map.ConfigMap;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class YamlParser implements ConfigReader, ConfigWriter {
    private final Yaml yamlBackend;

    private final @NotNull MultiConfigHandler parent;
    private final @NotNull Set<@NotNull Throwable> errors;

    private final @NotNull YamlCommentParser commentParser;

    public YamlParser(final @NotNull MultiConfigHandler parent) {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        this.yamlBackend = new Yaml(options);

        this.parent = parent;
        this.errors = new HashSet<>();

        this.commentParser = new YamlCommentParser(this);
    }

    @Override public @NotNull Set<@NotNull Throwable> getErrors() {
        return Collections.unmodifiableSet(this.errors);
    }

    @Override public @NotNull String getName() {
        return "YAML";
    }

    @Override public @NotNull Optional<@NotNull ConfigMap> read(@NotNull Path fileLocation) {
        if (this.errors.isEmpty()) {
            final Optional<String> yaml = this.commentParser.readCommentsFromFile(fileLocation);

            if (yaml.isPresent() && this.commentParser.getErrors().isEmpty()) {
                try {
                    final String json =
                            this.parent.getGsonBackend().toJson((Map<?, ?>) this.yamlBackend.load(yaml.get()));

                    return Optional.of(
                            this.parent
                                    .getGsonBackend()
                                    .fromJson(json, new TypeToken<ConfigMap>() {}.getType())
                    );
                } catch (Exception e) {
                    this.errors.add(e);
                }
            } else {
                this.errors.addAll(this.commentParser.getErrors());
            }
        }

        return Optional.empty();
    }

    @Override public boolean write(
            @NotNull Path fileLocation, @NotNull ConfigMap configMap
    ) {
        if (this.errors.isEmpty()) {
            final Optional<String> yaml = this.commentParser.writeCommentsToString(configMap);

            if (yaml.isPresent() && this.commentParser.getErrors().isEmpty()) {
                try {
                    Files.write(
                            fileLocation,
                            yaml.get().getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.TRUNCATE_EXISTING
                    );
                } catch (Exception e) {
                    this.errors.add(e);
                }
            } else {
                this.errors.addAll(this.commentParser.getErrors());
            }
        }

        return this.errors.isEmpty();
    }

    public Yaml getYamlBackend() {
        return this.yamlBackend;
    }
}
