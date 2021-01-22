package com.heretere.hch.core.internal.comments;

import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public interface CommentReader extends CommentAdapter {
    @NotNull
    Optional<@NotNull String> readCommentsFromFile(@NotNull Path fileLocation);
}
