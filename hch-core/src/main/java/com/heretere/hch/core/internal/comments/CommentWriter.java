package com.heretere.hch.core.internal.comments;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.internal.map.ConfigMap;

public interface CommentWriter extends CommentAdapter {
    @NotNull
    Optional<@NotNull String> writeCommentsToFile(@NotNull ConfigMap configMap);
}
