package com.heretere.hch.core.backend.comments;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.backend.map.ConfigMap;

public interface CommentWriter extends CommentAdapter {
    @NotNull
    Optional<@NotNull String> writeCommentsToString(@NotNull ConfigMap configMap);
}
