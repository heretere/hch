package com.heretere.hch.core.backend.config;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.backend.ErrorHolder;

public interface ConfigAdapter extends ErrorHolder {
    @NotNull
    String getName();
}
