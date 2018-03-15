package com.finix.gateway.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.finix.gateway.common.Constants.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Objects.toStringHelper;

import lombok.Data;

@Data
public class ConnectionPoolConfig {
	
    @JsonProperty("maxConnectionsPerHost")
    private final int maxConnectionsPerHost;
    @JsonProperty("maxPendingConnectionsPerHost")
    private final int maxPendingConnectionsPerHost;
    @JsonProperty("connectTimeoutMillis")
    private final int connectTimeoutMillis;
    @JsonProperty("socketTimeoutMillis")
    private final int socketTimeoutMillis;
    @JsonProperty("pendingConnectionTimeoutMillis")
    private final int pendingConnectionTimeoutMillis;

    @JsonCreator
    public ConnectionPoolConfig(@JsonProperty("maxConnectionsPerHost") Integer maxConnectionsPerHost,
                           @JsonProperty("maxPendingConnectionsPerHost") Integer maxPendingConnectionsPerHost,
                           @JsonProperty("connectTimeoutMillis") Integer connectTimeoutMillis,
                           @JsonProperty("socketTimeoutMillis") Integer socketTimeoutMillis,
                           @JsonProperty("pendingConnectionTimeoutMillis") Integer pendingConnectionTimeoutMillis
                           ) {
        this.maxConnectionsPerHost = firstNonNull(maxConnectionsPerHost, DEFAULT_MAX_CONNECTIONS_PER_HOST);
        this.maxPendingConnectionsPerHost = firstNonNull(maxPendingConnectionsPerHost, DEFAULT_MAX_PENDING_CONNECTIONS_PER_HOST);
        this.connectTimeoutMillis = firstNonNull(connectTimeoutMillis, DEFAULT_CONNECT_TIMEOUT_MILLIS);
        this.socketTimeoutMillis = firstNonNull(socketTimeoutMillis, DEFAULT_SOCKET_TIMEOUT_MILLIS);
        this.pendingConnectionTimeoutMillis = firstNonNull(pendingConnectionTimeoutMillis, DEFAULT_CONNECT_TIMEOUT_MILLIS);
    }

    public ConnectionPoolConfig(int maxConnectionsPerHost,
                           int maxPendingConnectionsPerHost,
                           int connectTimeoutMillis,
                           int socketTimeoutMillis,
                           int pendingConnectionTimeoutMillis) {
        this.maxConnectionsPerHost = firstNonNull(maxConnectionsPerHost, DEFAULT_MAX_CONNECTIONS_PER_HOST);
        this.maxPendingConnectionsPerHost = firstNonNull(maxPendingConnectionsPerHost, DEFAULT_MAX_PENDING_CONNECTIONS_PER_HOST);
        this.connectTimeoutMillis = firstNonNull(connectTimeoutMillis, DEFAULT_CONNECT_TIMEOUT_MILLIS);
        this.socketTimeoutMillis = firstNonNull(socketTimeoutMillis, DEFAULT_SOCKET_TIMEOUT_MILLIS);
        this.pendingConnectionTimeoutMillis = firstNonNull(pendingConnectionTimeoutMillis, DEFAULT_CONNECT_TIMEOUT_MILLIS);
    }
    
    public ConnectionPoolConfig(Builder builder) {
        this(
                builder.maxConnectionsPerHost,
                builder.maxPendingConnectionsPerHost,
                builder.connectTimeoutMillis,
                builder.socketTimeoutMillis,
                builder.pendingConnectionTimeoutMillis
                );
    }
    
    public static ConnectionPoolConfig defaultConnectionPoolConfig() {
        return new ConnectionPoolConfig(new Builder());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(maxConnectionsPerHost, maxPendingConnectionsPerHost, connectTimeoutMillis,
                socketTimeoutMillis, pendingConnectionTimeoutMillis);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ConnectionPoolConfig other = (ConnectionPoolConfig) obj;
        return Objects.equals(this.maxConnectionsPerHost, other.maxConnectionsPerHost)
                && Objects.equals(this.maxPendingConnectionsPerHost, other.maxPendingConnectionsPerHost)
                && Objects.equals(this.connectTimeoutMillis, other.connectTimeoutMillis)
                && Objects.equals(this.socketTimeoutMillis, other.socketTimeoutMillis)
                && Objects.equals(this.pendingConnectionTimeoutMillis, other.pendingConnectionTimeoutMillis);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("maxConnectionsPerHost", maxConnectionsPerHost)
                .add("maxPendingConnectionsPerHost", maxPendingConnectionsPerHost)
                .add("connectTimeoutMillis", connectTimeoutMillis)
                .add("socketTimeoutMillis", socketTimeoutMillis)
                .add("pendingConnectionTimeoutMillis", pendingConnectionTimeoutMillis)
                .toString();
    }
    
    
    
    public static final class Builder {
        private int maxConnectionsPerHost = DEFAULT_MAX_CONNECTIONS_PER_HOST;
        private int maxPendingConnectionsPerHost = DEFAULT_MAX_PENDING_CONNECTIONS_PER_HOST;
        private int connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
        private int socketTimeoutMillis = DEFAULT_SOCKET_TIMEOUT_MILLIS;
        private int pendingConnectionTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;

        /**
         * Constructs an instance with default settings.
         */
        public Builder() {
        }

        
        public Builder(ConnectionPoolConfig connectionPoolConfig) {
            this.maxConnectionsPerHost = connectionPoolConfig.maxConnectionsPerHost;
            this.maxPendingConnectionsPerHost = connectionPoolConfig.maxPendingConnectionsPerHost;
            this.connectTimeoutMillis = connectionPoolConfig.connectTimeoutMillis;
            this.socketTimeoutMillis = connectionPoolConfig.socketTimeoutMillis;
            this.pendingConnectionTimeoutMillis = connectionPoolConfig.pendingConnectionTimeoutMillis;
        }
        
        /**
         * Sets the maximum number of active connections for a single hosts's connection pool.
         *
         * @param maxConnectionsPerHost maximum number of active connections
         * @return this builder
         */
        public Builder maxConnectionsPerHost(int maxConnectionsPerHost) {
            this.maxConnectionsPerHost = maxConnectionsPerHost;
            return this;
        }

        /**
         * Sets the maximum allowed number of consumers, per host, waiting for a connection.
         *
         * @param maxPendingConnectionsPerHost maximum number of consumers
         * @return this builder
         */
        public Builder maxPendingConnectionsPerHost(int maxPendingConnectionsPerHost) {
            this.maxPendingConnectionsPerHost = maxPendingConnectionsPerHost;
            return this;
        }

        /**
         * Sets socket read timeout.
         *
         * @param socketTimeout read timeout
         * @param timeUnit unit of timeout
         * @return this builder
         */
        public Builder socketTimeout(int socketTimeout, TimeUnit timeUnit) {
            this.socketTimeoutMillis = (int) timeUnit.toMillis(socketTimeout);
            return this;
        }

        /**
         * Sets socket connect timeout.
         *
         * @param connectTimeout connect timeout
         * @param timeUnit unit of timeout
         * @return this builder
         */
        public Builder connectTimeout(int connectTimeout, TimeUnit timeUnit) {
            this.connectTimeoutMillis = (int) timeUnit.toMillis(connectTimeout);
            return this;
        }

        /**
         * Sets the maximum wait time for pending consumers.
         *
         * @param waitTimeout timeout
         * @param timeUnit unit that timeout is measured in
         * @return this builder
         */
        public Builder pendingConnectionTimeout(int waitTimeout, TimeUnit timeUnit) {
            this.pendingConnectionTimeoutMillis = (int) timeUnit.toMillis(waitTimeout);
            return this;
        }

        /**
         * Constructs a new instance with the configured settings.
         *
         * @return a new instance
         */
        public ConnectionPoolConfig build() {
            return new ConnectionPoolConfig(this);
        }
        
    }
    
}
