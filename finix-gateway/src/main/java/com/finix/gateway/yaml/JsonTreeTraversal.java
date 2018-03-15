package com.finix.gateway.yaml;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Traverse JSON trees.
 */
class JsonTreeTraversal {
    public static <T extends JsonTreeVisitor> T traverseJsonTree(JsonNode node, T visitor) {
        traverseJsonTree(node, null, new ArrayList<>(), visitor);
        return visitor;
    }

    private static void traverseJsonTree(JsonNode node, ContainerNode<?> parent, List<PathElement> path, JsonTreeVisitor visitor) {
        if (node.isValueNode()) {
            visitor.onValueNode((ValueNode) node, Optional.of(parent), ImmutableList.copyOf(path));
        } else if (node.isArray()) {
            Iterable<JsonNode> elements = node::elements;
            int index = 0;
            for (JsonNode child : elements) {
                path.add(new ArrayIndex(index));
                traverseJsonTree(child, (ArrayNode) node, path, visitor);
                path.remove(path.size() - 1);

                index++;
            }
        } else if (node.isObject()) {
            Iterable<Map.Entry<String, JsonNode>> fields = node::fields;

            for (Map.Entry<String, JsonNode> field : fields) {
                String fieldName = field.getKey();
                JsonNode child = field.getValue();

                path.add(new ObjectField(fieldName));
                traverseJsonTree(child, (ObjectNode) node, path, visitor);
                path.remove(path.size() - 1);
            }
        } else {
            throw new IllegalStateException("Unhandled node type: " + node.getNodeType());
        }
    }

    /**
     * Callback for receiving tree traversal events.
     */
    public interface JsonTreeVisitor {
        void onValueNode(ValueNode node, Optional<ContainerNode<?>> parent, List<PathElement> path);
    }
}