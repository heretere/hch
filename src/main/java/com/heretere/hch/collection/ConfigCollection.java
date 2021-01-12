package com.heretere.hch.collection;

import com.heretere.hch.generics.TypeDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ConfigCollection<T> extends TypeDefinition<T> {
    void add(T type);

    void remove(T type);

    @NotNull Collection<@NotNull T> getBackingCollection();
}
