package com.finix.gateway.yaml;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Object field - a type of path element.
 */
final class ObjectField implements PathElement {
    private final String name;

    public ObjectField(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public void setChild(JsonNode parent, JsonNode child) {
        ((ObjectNode) parent).set(name, child);
    }

    @Override
    public JsonNode child(JsonNode parent) {
        return parent.get(name);
    }

    @Override
    public boolean isArrayIndex() {
        return false;
    }

    @Override
    public boolean isObjectField() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}