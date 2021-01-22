package com.heretere.hch.core.internal.config;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.internal.ErrorHolder;

public interface ConfigAdapter extends ErrorHolder {
    @NotNull
    String getName();
}
