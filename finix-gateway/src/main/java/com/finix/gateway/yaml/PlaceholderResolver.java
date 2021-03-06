package com.finix.gateway.yaml;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;
import static com.finix.gateway.yaml.JsonTreeTraversal.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.finix.gateway.yaml.JsonTreeTraversal.JsonTreeVisitor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;

/**
 * Resolves placeholders in JSON trees.
 */
public class PlaceholderResolver {
    private static final Pattern PLACEHOLDER_REGEX_CAPTURE_ALL = compile("(\\$\\{[_a-zA-Z0-9\\[\\]\\.]+(:[^\\}]*?)?\\})");
    private static final Pattern PLACEHOLDER_REGEX_CAPTURE_NAME_AND_DEFAULT = compile("\\$\\{([_a-zA-Z0-9\\[\\]\\.]+)(?::([^\\}]*?))?\\}");

    private final ObjectNode rootNode;
    private final Map<String, String> externalProperties;

    public PlaceholderResolver(ObjectNode rootNode, Map<String, String> externalProperties) {
        this.rootNode = checkNotNull(rootNode);
        this.externalProperties = ImmutableMap.copyOf(externalProperties);
    }

    /**
     * Resolve placeholders. Note that this modifies the original root node.
     *
     * @param rootNode           root node
     * @param externalProperties properties
     * @return unresolved placeholders
     */
    public static Collection<UnresolvedPlaceholder> resolvePlaceholders(JsonNode rootNode, Map<String, String> externalProperties) {
        if (rootNode.isObject()) {
            return new PlaceholderResolver((ObjectNode) rootNode, externalProperties).resolve();
        }
        return Collections.emptyList();
    }

    /**
     * Resolve placeholders. Note that this modifies the original root node.
     *
     * @param rootNode   root node
     * @param properties properties
     * @return unresolved placeholders
     */
    public static Collection<UnresolvedPlaceholder> resolvePlaceholders(JsonNode rootNode, Properties properties) {
        return resolvePlaceholders(rootNode, (Map) properties);
    }

    public Collection<UnresolvedPlaceholder> resolve() {
        Map<String, String> resolved = newHashMap(externalProperties);

        ResolutionVisitor visitor = new ResolutionVisitor(resolved);

        while (visitor.passAgain) {
            visitor.passAgain = false;
            traverseJsonTree(rootNode, visitor);
        }

        traverseJsonTree(rootNode, new DefaultsVisitor());

        return traverseJsonTree(rootNode, new UnresolvedPlaceholdersVisitor()).unresolvedPlaceholderDescriptions;
    }

    private static class ResolutionVisitor implements JsonTreeVisitor {
        private final Map<String, String> resolved;
        private boolean passAgain = true;

        public ResolutionVisitor(Map<String, String> resolved) {
            this.resolved = resolved;
        }

        @Override
        public void onValueNode(ValueNode node, Optional<ContainerNode<?>> parent, List<PathElement> pathAsList) {
            String path = new NodePath(pathAsList).toString();

            String value = node.isTextual() ? node.textValue() : node.toString();

            List<Placeholder> placeholders = extractPlaceholders(value);

            if (placeholders.isEmpty() && !resolved.containsKey(path)) {
                resolved.put(path, value);
                passAgain = true;
            } else {
                boolean changes = false;

                for (Placeholder placeholder : placeholders) {
                    String replacement = resolved.get(placeholder.name());

                    if (replacement != null) {
                        String valueWithReplacements = replacePlaceholder(value, placeholder.name(), replacement);

                        last(pathAsList).setChild(parent.get(), TextNode.valueOf(valueWithReplacements));

                        value = valueWithReplacements;
                        changes = true;
                    }
                }
                passAgain |= changes;
            }
        }


    }

    private static class DefaultsVisitor implements JsonTreeVisitor {
        @Override
        public void onValueNode(ValueNode node, Optional<ContainerNode<?>> parent, List<PathElement> path) {
            String value = node.isTextual() ? node.textValue() : node.toString();

            List<Placeholder> placeholders = extractPlaceholders(value);

            if (!placeholders.isEmpty()) {
                for (Placeholder placeholder : placeholders) {
                    if (placeholder.hasDefaultValue()) {
                        String valueWithReplacements = replacePlaceholder(value, placeholder.name(), placeholder.defaultValue());

                        last(path).setChild(parent.get(), TextNode.valueOf(valueWithReplacements));
                    }
                }
            }
        }
    }

    private static class UnresolvedPlaceholdersVisitor implements JsonTreeVisitor {
        private final Collection<UnresolvedPlaceholder> unresolvedPlaceholderDescriptions = new ArrayList<>();

        @Override
        public void onValueNode(ValueNode node, Optional<ContainerNode<?>> parent, List<PathElement> path) {
            String value = node.isTextual() ? node.textValue() : node.toString();

            List<String> placeholders = extractPlaceholderStrings(value);

            if (!placeholders.isEmpty()) {
                String pathString = new NodePath(path).toString();

                for (String placeholder : placeholders) {
                    unresolvedPlaceholderDescriptions.add(new UnresolvedPlaceholder(pathString, value, placeholder));
                }
            }
        }
    }

    /**
     * UnresolvedPlaceholder.
     */
    public static final class UnresolvedPlaceholder {
        private final String path;
        private final String value;
        private final String placeholder;

        private UnresolvedPlaceholder(String path, String value, String placeholder) {
            this.path = path;
            this.value = value;
            this.placeholder = placeholder;
        }

        public String path() {
            return path;
        }

        public String value() {
            return value;
        }

        public String placeholder() {
            return placeholder;
        }

        @Override
        public String toString() {
            return format("%s in %s=%s", placeholder, path, value);
        }
    }

    private static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }

    @VisibleForTesting
    public static String replacePlaceholder(String originalString, String placeholderName, String placeholderValue) {
        return originalString.replaceAll("\\$\\{" + quote(placeholderName) + "(?:\\:[^\\}]*?)?\\}", placeholderValue);
    }

    @VisibleForTesting
    static List<String> extractPlaceholderStrings(String value) {
        Matcher matcher = PLACEHOLDER_REGEX_CAPTURE_ALL.matcher(value);

        List<String> placeholders = new ArrayList<>();

        while (matcher.find()) {
            placeholders.add(matcher.group());
        }

        return placeholders;
    }

    @VisibleForTesting
    public static List<Placeholder> extractPlaceholders(String value) {
        List<Placeholder> namesAndDefaults = new ArrayList<>();

        for (String placeholder : extractPlaceholderStrings(value)) {
            Matcher placeholderMatcher = PLACEHOLDER_REGEX_CAPTURE_NAME_AND_DEFAULT.matcher(placeholder);

            if (placeholderMatcher.matches()) {
                namesAndDefaults.add(new Placeholder(placeholderMatcher.group(1), placeholderMatcher.group(2)));
            }
        }

        return namesAndDefaults;
    }

    /**
     * A name, value pair.
     */
    public static class Placeholder {
        private final String name;
        private final Optional<String> defaultValue;

        Placeholder(String name, Optional<String> defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        Placeholder(String name, String defaultValue) {
            this(name, Optional.ofNullable(defaultValue));
        }

        Placeholder(String name) {
            this(name, Optional.empty());
        }

        public String name() {
            return name;
        }

        public boolean hasDefaultValue() {
            return defaultValue.isPresent();
        }

        public String defaultValue() {
            return defaultValue.get();
        }

        @Override
        public String toString() {
            if (defaultValue.isPresent()) {
                return "${" + name + ":" + defaultValue.get() + "}";
            }

            return "${" + name + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Placeholder that = (Placeholder) o;
            return Objects.equals(name, that.name) && Objects.equals(defaultValue, that.defaultValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, defaultValue);
        }
    }
}
