/*
 * MIT License
 *
 * Copyright (c) 2021 Justin Heflin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.heretere.hch.processor;

import com.heretere.hch.processor.exception.InvalidTypeException;
import org.jetbrains.annotations.NotNull;

/**
 * Used to deserialize config keys from a backend processor
 *
 * @param <T> The backend processor type
 * @param <K> The deserialized value.
 */
public interface TypeDeserializer<T, K> extends TypeHandler<K> {
    /**
     * Deserialized a config key to a value.
     *
     * @param parser    The processor backend.
     * @param exactType The exact type of the deserialized object.
     * @param key       The key to deserialize in the processor backend.
     * @return The deserialized value.
     * @throws InvalidTypeException If there is an issue deserializing the key.
     */
    @NotNull K deserialize(
        @NotNull T parser,
        @NotNull Class<?> exactType,
        @NotNull String key
    ) throws InvalidTypeException;

    /**
     * @param exactType The generic type of the passed in value
     * @param value     The value
     * @return A value that is attempted to be converted to the generic type of this class.
     * @throws InvalidTypeException When deserialization fails
     */
    @NotNull K deserializeRaw(
        @NotNull Class<?> exactType,
        @NotNull Object value
    ) throws InvalidTypeException;
}
