package com.heretere.hch.processor.toml.typehandler;

import com.google.common.collect.Lists;
import com.heretere.hch.processor.MultiSerializer;
import com.heretere.hch.processor.exception.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlParseResult;

import java.util.List;

public class TomlFloatSerializer implements MultiSerializer<TomlParseResult, Float> {
    @Override public @NotNull Class<Float> getGenericType() {
        return Float.class;
    }

    @Override public @NotNull Float deserialize(
        @NotNull TomlParseResult parser,
        @NotNull Class<?> exactType,
        @NotNull String key
    ) throws InvalidTypeException {
        if (!parser.isDouble(key)) {
            throw new InvalidTypeException();
        }

        Double output = parser.getDouble(key);

        if (output == null) {
            throw new InvalidTypeException();
        }

        return output.floatValue();
    }

    @Override public @NotNull Float deserializeRaw(
        @NotNull Class<?> exactType,
        @NotNull Object value
    ) throws InvalidTypeException {
        return Float.valueOf(value.toString());
    }

    @Override public @NotNull List<String> serialize(@NotNull Object value) {
        return Lists.newArrayList(value.toString());
    }
}
