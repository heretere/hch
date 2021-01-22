package com.heretere.hch.yaml;

import com.heretere.hch.core.backend.comments.CommentReader;
import com.heretere.hch.core.backend.comments.CommentWriter;
import com.heretere.hch.core.backend.map.ConfigMap;
import com.heretere.hch.core.backend.util.ConfigMapperUtils;
import org.jetbrains.annotations.NotNull;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YamlCommentParser implements CommentReader, CommentWriter {
    private static final String COMMENT_PATTERN = "#";
    private static final Pattern KEY_PATTERN = Pattern.compile("^(.*):.*$");

    private final @NotNull YamlParser yamlParser;
    private final @NotNull Set<@NotNull Throwable> errors;

    public YamlCommentParser(
            final @NotNull YamlParser yamlParser
    ) {
        this.yamlParser = yamlParser;
        this.errors = new HashSet<>();
    }

    private static String getIndent(final @NotNull String string) {
        StringBuilder s = new StringBuilder();

        for (char character : string.toCharArray()) {
            if (!Character.isSpaceChar(character)) {
                break;
            }

            s.append(' ');
        }

        return s.toString();
    }

    @Override public @NotNull Set<@NotNull Throwable> getErrors() {
        return Collections.unmodifiableSet(this.errors);
    }

    @Override public @NotNull Optional<@NotNull String> readCommentsFromFile(@NotNull Path fileLocation) {
        if (this.errors.isEmpty()) {
            try (Stream<String> lines = Files.lines(fileLocation, StandardCharsets.UTF_8)) {
                final StringBuilder output = new StringBuilder();
                final List<String> prependComments = new ArrayList<>();

                lines.forEach(line -> {
                    if (line.trim().startsWith(YamlCommentParser.COMMENT_PATTERN)) {
                        prependComments.add(line);
                    } else {
                        final Matcher matcher = YamlCommentParser.KEY_PATTERN.matcher(line);

                        if (!matcher.matches()) {
                            output.append(line).append(System.lineSeparator());
                            return;
                        }

                        final String key = matcher.group(1).trim().replace("\"", "");
                        final String indent = YamlCommentParser.getIndent(line);

                        if (!prependComments.isEmpty()) {
                            output
                                    .append(indent)
                                    .append(key)
                                    .append("_comments:")
                                    .append(System.lineSeparator())
                                    .append(
                                            new BufferedReader(
                                                    new StringReader(this.yamlParser
                                                            .getYamlBackend()
                                                            .dump(prependComments)
                                                    ))
                                                    .lines()
                                                    .map(comment -> indent + "  " + comment)
                                                    .collect(Collectors.joining(System.lineSeparator()))
                                    )
                                    .append(System.lineSeparator());

                            prependComments.clear();
                        }

                        output.append(line).append(System.lineSeparator());
                    }
                });

                return Optional.of(output.toString());
            } catch (Exception e) {
                this.errors.add(e);
            }
        }

        return Optional.empty();
    }

    @Override public @NotNull Optional<@NotNull String> writeCommentsToString(@NotNull ConfigMap configMap) {
        if (this.errors.isEmpty()) {
            try {
                final List<SimpleImmutableEntry<String, List<String>>> comments = ConfigMapperUtils.extractComments(
                        new ArrayList<>(),
                        configMap
                );

                final String yaml = this.yamlParser.getYamlBackend().dump(configMap);
                final StringBuilder output = new StringBuilder();

                new BufferedReader(new StringReader(yaml))
                        .lines()
                        .forEach(line -> {
                            final Matcher matcher = YamlCommentParser.KEY_PATTERN.matcher(line);

                            if (!matcher.matches()) {
                                output.append(line).append(System.lineSeparator());
                                return;
                            }

                            comments
                                    .stream()
                                    .filter(entry ->
                                            ConfigMapperUtils.stripWhiteSpace(entry.getKey())
                                                    .equals(ConfigMapperUtils.stripWhiteSpace(matcher.group(1)))
                                    )
                                    .findFirst()
                                    .ifPresent(entry -> {
                                        entry.getValue().forEach(comment -> output.append(comment)
                                                .append(System.lineSeparator()));
                                        comments.remove(entry);
                                    });

                            output.append(line).append(System.lineSeparator());
                        });

                return Optional.of(output.toString());
            } catch (Exception e) {
                this.errors.add(e);
            }
        }

        return Optional.empty();
    }
}
