package com.heretere.hch.json;

import com.google.gson.reflect.TypeToken;
import com.heretere.hch.core.MultiConfigHandler;
import com.heretere.hch.core.backend.config.ConfigReader;
import com.heretere.hch.core.backend.config.ConfigWriter;
import com.heretere.hch.core.backend.map.ConfigMap;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JsonParser implements ConfigReader, ConfigWriter {
    private final @NotNull MultiConfigHandler parent;
    private final @NotNull Set<@NotNull Throwable> errors;

    private final @NotNull JsonCommentParser commentParser;

    public JsonParser(final @NotNull MultiConfigHandler parent) {
        this.parent = parent;
        this.errors = new HashSet<>();
        this.commentParser = new JsonCommentParser(parent);
    }

    @Override
    public @NotNull Optional<@NotNull ConfigMap> read(final @NotNull Path fileLocation) {
        if (fileLocation.toFile().length() == 0) {
            return Optional.of(new ConfigMap());
        }

        if (this.errors.isEmpty()) {
            final Optional<String> json = this.commentParser.readCommentsFromFile(fileLocation);

            if (json.isPresent() && this.commentParser.getErrors().isEmpty()) {
                try {
                    return Optional.of(
                        this.parent
                            .getGsonBackend()
                            .fromJson(json.get(), new TypeToken<ConfigMap>() {}.getType())
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

    @Override
    public boolean write(
            final @NotNull Path fileLocation,
            final @NotNull ConfigMap configMap
    ) {
        if (this.errors.isEmpty()) {
            final Optional<String> json = this.commentParser.writeCommentsToString(configMap);

            if (json.isPresent() && this.commentParser.getErrors().isEmpty()) {
                try {
                    Files.write(
                        fileLocation,
                        json.get().getBytes(StandardCharsets.UTF_8),
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

    @Override
    public @NotNull Set<@NotNull Throwable> getErrors() {
        return Collections.unmodifiableSet(this.errors);
    }

    @Override
    public @NotNull String getName() {
        return "JSON";
    }
}
