package com.finix.gateway.config;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import lombok.Data;

@Data
public class HttpConfig {

    private int port;

    public HttpConfig(@JsonProperty("port") Integer port) {
        this.port = port;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(port);
    }
    
    public String type() {
        return "http";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final HttpConfig other = (HttpConfig) obj;
        return Objects.equal(this.port, other.port);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("port", port)
                .toString();
    }
}
