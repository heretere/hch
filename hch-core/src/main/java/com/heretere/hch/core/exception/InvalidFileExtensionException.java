package com.heretere.hch.core.exception;

import org.jetbrains.annotations.NotNull;

public class InvalidFileExtensionException extends RuntimeException {
    public InvalidFileExtensionException(final @NotNull String message) {
        super(message);
    }
}
