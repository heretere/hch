package com.heretere.hch.core.internal;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

public interface ErrorHolder {
    @NotNull
    Set<@NotNull Throwable> getErrors();
}
