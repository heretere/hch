package com.heretere.hch;

import com.google.common.collect.Maps;
import com.heretere.hch.processor.Processor;
import com.heretere.hch.structure.backend.ConfigClassParser;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
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
}
