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

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * This is used to create list types.
 *
 * @param <T> The generic type of the list.
 */
public class ConfigList<T> implements ConfigCollection<T> {
    private final @NotNull Class<T> type;
    private final @NotNull List<@NotNull T> backingList;

    private ConfigList(
        final @NotNull Class<T> type,
        final @NotNull List<@NotNull T> backingList
    ) {
        this.type = type;
        this.backingList = backingList;
    }

    /**
     * @param type        The class of the generic type.
     * @param backendList The backend list implementation.
     * @param <T>         The generic type of the list.
     * @return A new config list instance
     */
    @SuppressWarnings("unchecked")
    public static <T> ConfigList<T> newInstance(
        final @NotNull Class<T> type,
        final @NotNull Collection<?> backendList
    ) {
        return new ConfigList<>(type, (List<T>) backendList);
    }

    @Override public @NotNull Class<T> getGenericType() {
        return this.type;
    }

    @Override public void add(T type) {
        this.backingList.add(type);
    }

    @Override public void remove(T type) {
        this.backingList.remove(type);
    }

    @Override public @NotNull Collection<@NotNull T> getBackingCollection() {
        return this.backingList;
    }
}
