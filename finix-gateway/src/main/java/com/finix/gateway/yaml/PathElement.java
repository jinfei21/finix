package com.finix.gateway.yaml;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An element in the path representing a position in the tree (i.e. object field or array index).
 */
interface PathElement {
    void setChild(JsonNode parent, JsonNode child);

    JsonNode child(JsonNode parent);

    boolean isArrayIndex();

    boolean isObjectField();
}
