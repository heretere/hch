package com.heretere.hch.processor.toml.typehandler;

import com.google.common.collect.Lists;
import com.heretere.hch.processor.MultiSerializer;
import com.heretere.hch.processor.exception.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlParseResult;

import java.util.List;

public final class TomlDoubleSerializer implements MultiSerializer<TomlParseResult, Double> {
    @Override public @NotNull Class<Double> getGenericType() {
        return Double.class;
    }

    @Override public @NotNull Double deserialize(
        final @NotNull TomlParseResult parser,
        final @NotNull Class<?> exactType,
        final @NotNull String key
    ) throws InvalidTypeException {
        if (!parser.isDouble(key)) {
            throw new InvalidTypeException();
        }

        Double output = parser.getDouble(key);

        if (output == null) {
            throw new InvalidTypeException();
        }

        return output;
    }

    @Override public @NotNull Double deserializeRaw(
        final @NotNull Class<?> exactType,
        final @NotNull Object value
    ) throws InvalidTypeException {
        return Double.valueOf(value.toString());
    }

    @Override public @NotNull List<String> serialize(@NotNull Object value) {
        return Lists.newArrayList(value.toString());
    }
}
