package com.heretere.hch.core.exception;

import org.jetbrains.annotations.NotNull;

public class InvalidPojoException extends RuntimeException {
    public InvalidPojoException(final @NotNull String message) {
        super(message);
    }
}
