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

package com.heretere.hch.collection;

import com.heretere.hch.generics.TypeDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is used to create list types.
 *
 * @param <T> The generic type of the list.
 */
public class ConfigList<T> extends ArrayList<T> implements TypeDefinition<T> {
    private final @NotNull Class<T> type;

    private ConfigList(final @NotNull Class<T> type) {
        this.type = type;
    }

    /**
     * @param type     The class of the generic type.
     * @param elements The elements to add to the list.
     * @param <T>      The generic type of the list.
     * @return A new config list instance
     */
    @SafeVarargs public static <T> ConfigList<T> newInstance(
        final @NotNull Class<T> type,
        final @NotNull T... elements
    ) {
        ConfigList<T> list = new ConfigList<>(type);
        list.addAll(Arrays.asList(elements));
        return list;
    }

    @Override public @NotNull Class<T> getGenericType() {
        return this.type;
    }
}
