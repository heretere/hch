package com.heretere.hch.json;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.heretere.hch.core.MultiConfigHandler;
import com.heretere.hch.core.internal.comments.CommentReader;
import com.heretere.hch.core.internal.comments.CommentWriter;
import com.heretere.hch.core.internal.map.ConfigMap;
import com.heretere.hch.core.internal.util.ConfigMapperUtils;

public class JsonCommentParser implements CommentReader, CommentWriter {
    private static final String COMMENT_PATTERN = "//";
    private static final Pattern KEY_PATTERN = Pattern.compile("^(.*):.*$");
    private static final Pattern EMPTY_SPACE_PATTERN = Pattern.compile("[\\s|\"]+");

    private final @NotNull MultiConfigHandler parent;
    private final @NotNull Set<@NotNull Throwable> errors;

    public JsonCommentParser(final @NotNull MultiConfigHandler parent) {
        this.parent = parent;
        this.errors = new HashSet<>();
    }

    @Override
    public @NotNull Optional<@NotNull String> readCommentsFromFile(final @NotNull Path fileLocation) {
        try (Stream<String> lines = Files.lines(fileLocation, StandardCharsets.UTF_8)) {
            final StringBuilder output = new StringBuilder();
            final List<String> prependComments = new ArrayList<>();

            lines.forEach(line -> {
                if (line.trim().startsWith(JsonCommentParser.COMMENT_PATTERN)) {
                    prependComments.add(line);
                } else {
                    final Matcher matcher = JsonCommentParser.KEY_PATTERN.matcher(line);

                    if (!matcher.matches()) {
                        output.append(line).append(System.lineSeparator());
                        return;
                    }

                    final String key = matcher.group(1).trim().replace("\"", "");

                    if (!prependComments.isEmpty()) {
                        output
                            .append("\"")
                            .append(key)
                            .append("_comments\": ")
                            .append(this.parent.getGsonBackend().toJson(prependComments))
                            .append(",")
                            .append(System.lineSeparator());

                        prependComments.clear();
                    }

                    output.append(line).append(System.lineSeparator());
                }
            });

            return Optional.of(output.toString());
        } catch (Exception e) {
            e.printStackTrace();
            this.errors.add(e);
        }

        return Optional.empty();
    }

    @Override
    public @NotNull Optional<@NotNull String> writeCommentsToFile(final @NotNull ConfigMap configMap) {
        try {
            final List<SimpleImmutableEntry<String, List<String>>> comments = ConfigMapperUtils.extractComments(
                new ArrayList<>(),
                configMap
            );

            final String json = this.parent.getGsonBackend().toJson(configMap);
            final StringBuilder output = new StringBuilder();

            new BufferedReader(new StringReader(json))
                .lines()
                .forEach(line -> {
                    final Matcher matcher = JsonCommentParser.KEY_PATTERN.matcher(line);

                    if (!matcher.matches()) {
                        output.append(line).append(System.lineSeparator());
                        return;
                    }

                    comments
                        .stream()
                        .findFirst()
                        .ifPresent(entry -> {
                            if (
                                EMPTY_SPACE_PATTERN.matcher(entry.getKey())
                                    .replaceAll("")
                                    .equals(EMPTY_SPACE_PATTERN.matcher(matcher.group(1)).replaceAll(""))
                            ) {
                                entry.getValue()
                                    .forEach(comment -> output.append(comment).append(System.lineSeparator()));

                                comments.remove(0);
                            }
                        });

                    output.append(line).append(System.lineSeparator());
                });

            return Optional.of(output.toString());
        } catch (Exception e) {
            this.errors.add(e);
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Set<@NotNull Throwable> getErrors() {
        return Collections.unmodifiableSet(this.errors);
    }
}
