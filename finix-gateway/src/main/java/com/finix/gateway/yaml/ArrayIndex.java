package com.finix.gateway.yaml;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Array index - a type of path element.
 */
final class ArrayIndex implements PathElement {
    private final int index;

    public ArrayIndex(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    @Override
    public void setChild(JsonNode parent, JsonNode child) {
        ((ArrayNode) parent).set(index, child);
    }

    @Override
    public JsonNode child(JsonNode parent) {
        return parent.get(index);
    }

    @Override
    public boolean isArrayIndex() {
        return true;
    }

    @Override
    public boolean isObjectField() {
        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }
}
