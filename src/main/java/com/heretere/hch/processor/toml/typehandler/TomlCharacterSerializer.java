package com.heretere.hch.processor.toml.typehandler;

import com.google.common.collect.Lists;
import com.heretere.hch.processor.MultiSerializer;
import com.heretere.hch.processor.exception.InvalidTypeException;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlParseResult;

import java.util.List;

public class TomlCharacterSerializer implements MultiSerializer<TomlParseResult, Character> {
    @Override public @NotNull Class<Character> getGenericType() {
        return Character.class;
    }

    @Override public @NotNull Character deserialize(
        @NotNull TomlParseResult parser,
        @NotNull Class<?> exactType,
        @NotNull String key
    ) throws InvalidTypeException {
        if (!parser.isString(key)) {
            throw new InvalidTypeException();
        }

        String output = parser.getString(key);

        if (output == null) {
            throw new InvalidTypeException();
        }

        return output.charAt(0);
    }

    @Override public @NotNull Character deserializeRaw(
        @NotNull Class<?> exactType,
        @NotNull Object value
    ) throws InvalidTypeException {
        return value.toString().charAt(0);
    }

    @Override public @NotNull List<String> serialize(@NotNull Object value) {
        return Lists.newArrayList(value.toString());
    }
}
