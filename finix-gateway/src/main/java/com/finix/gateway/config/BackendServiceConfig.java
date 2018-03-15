package com.finix.gateway.config;

import static com.finix.gateway.config.ConnectionPoolConfig.defaultConnectionPoolConfig;
import static com.finix.gateway.config.HealthCheckConfig.noHealthCheck;
import static com.finix.gateway.config.StickySessionConfig.stickySessionDisabled;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.finix.gateway.common.Constants;
import com.finix.gateway.netty.common.Origin;
import com.google.common.collect.ImmutableSet;


public class BackendServiceConfig {

	private final String domain;
	private final String path;
	private final Set<Origin> origins;
    private final TlsSettings tlsSettings;
    private final HealthCheckConfig healthCheckConfig;
    private final StickySessionConfig stickySessionConfig;
    private final ConnectionPoolConfig connectionPoolConfig;
    private final int responseTimeoutMillis;

    
    /**
     * Creates an Application builder.
     *
     * @return a new builder
     */
    public static Builder newBackendServiceConfigBuilder() {
        return new Builder();
    }
    
    /**
     * Creates an Application builder that inherits from an existing Application.
     *
     * @param backendService application
     * @return a new builder
     */
    public static Builder newBackendServiceConfigBuilder(BackendServiceConfig backendServiceConfig) {
        return new Builder(backendServiceConfig);
    }
    
    private BackendServiceConfig(Builder builder) {
        this.domain = checkNotNull(builder.domain, "domain");
        this.path = checkNotNull(builder.path, "path");
        this.connectionPoolConfig = checkNotNull(builder.connectionPoolConfig);
        this.origins = ImmutableSet.copyOf(builder.origins);
        this.healthCheckConfig = checkNotNull(builder.healthCheckConfig);
        this.stickySessionConfig = checkNotNull(builder.stickySessionConfig);
        this.responseTimeoutMillis = builder.responseTimeoutMillis == 0
                ? Constants.DEFAULT_RESPONSE_TIMEOUT_MILLIS
                : builder.responseTimeoutMillis;
        this.tlsSettings = builder.tlsSettings;

        checkArgument(responseTimeoutMillis >= 0, "Request timeout must be greater than or equal to zero");
    }
    
    
    /**
     * Application builder.
     */
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
    public static final class Builder {
    	private String domain;
        private String path = "/";
        private Set<Origin> origins = emptySet();
        private ConnectionPoolConfig connectionPoolConfig = defaultConnectionPoolConfig();
        private StickySessionConfig stickySessionConfig = stickySessionDisabled();
        private HealthCheckConfig healthCheckConfig = noHealthCheck();
        public int responseTimeoutMillis = Constants.DEFAULT_RESPONSE_TIMEOUT_MILLIS;
        private TlsSettings tlsSettings;

        public Builder() {
        }

        private Builder(BackendServiceConfig backendServiceConfig) {
            this.domain = backendServiceConfig.domain;
            this.path = backendServiceConfig.path;
            this.origins = backendServiceConfig.origins;
            this.connectionPoolConfig = backendServiceConfig.connectionPoolConfig;
            this.stickySessionConfig = backendServiceConfig.stickySessionConfig;
            this.healthCheckConfig = backendServiceConfig.healthCheckConfig;
            this.responseTimeoutMillis = backendServiceConfig.responseTimeoutMillis;
            this.tlsSettings = backendServiceConfig.tlsSettings;
        }

        /**
         * Sets a path.
         *
         * @param path a path
         * @return this builder
         */
        @JsonProperty("path")
        public Builder path(String path) {
            this.path = checkValidPath(checkNotNull(path));
            return this;
        }

        private String checkValidPath(String path) {
            try {
                URI.create(path);
                return path;
            } catch (Throwable cause) {
                String message = format("Invalid path. Path='%s'", path);
                throw new IllegalArgumentException(message, cause);
            }
        }

        /**
         * Sets the response timeout in milliseconds.
         *
         * @param timeout a response timeout in milliseconds.
         * @return this builder
         */
        @JsonProperty("responseTimeoutMillis")
        public Builder responseTimeoutMillis(int timeout) {
            this.responseTimeoutMillis = timeout;
            return this;
        }

        /**
         * Sets hosts.
         *
         * @param origins origins
         * @return this builder
         */
        @JsonProperty("origins")
        public Builder origins(Set<Origin> origins) {
            this.origins = checkNotNull(origins);
            return this;
        }

        /**
         * Sets the https settings.
         * For Jackson JSON serialiser that de-serialises from Option<TlsSettings>.
         */
        Builder https(Optional<TlsSettings> tlsSettings) {
            this.tlsSettings = tlsSettings.orElse(null);
            return this;
        }

        /**
         * Sets the https settings.
         * For programmatic use
         */
        @JsonProperty("sslSettings")
        public Builder httpsOld(TlsSettings tlsSettings) {
            this.tlsSettings = tlsSettings;
            return this;
        }

        /**
         * Sets the https settings.
         * For programmatic use
         */
        @JsonProperty("tlsSettings")
        public Builder https(TlsSettings tlsSettings) {
            this.tlsSettings = tlsSettings;
            return this;
        }

        /**
         * Sets hosts.
         *
         * @param origins origins
         * @return this builder
         */
        public Builder origins(Origin... origins) {
            return origins(ImmutableSet.copyOf(origins));
        }


        /**
         * Sets connection pool configuration.
         *
         * @param connectionPoolSettings connection pool configuration
         * @return this builder
         */
        @JsonProperty("connectionPool")
        public Builder connectionPoolConfig(ConnectionPoolConfig connectionPoolConfig) {
            this.connectionPoolConfig = checkNotNull(connectionPoolConfig);
            return this;
        }


        /**
         * Sets sticky-session configuration.
         *
         * @param stickySessionConfig sticky-session configuration.
         * @return this builder
         */
        @JsonProperty("stickySession")
        public Builder stickySessionConfig(StickySessionConfig stickySessionConfig) {
            this.stickySessionConfig = checkNotNull(stickySessionConfig);
            return this;
        }

        /**
         * Sets health-check configuration.
         *
         * @param healthCheckConfig health-check configuration
         * @return this builder
         */
        @JsonProperty("healthCheck")
        public Builder healthCheckConfig(HealthCheckConfig healthCheckConfig) {
            this.healthCheckConfig = checkNotNull(healthCheckConfig);
            return this;
        }
        
        /**
         * Builds the application.
         *
         * @return the application
         */
        public BackendServiceConfig build() {
            return new BackendServiceConfig(this);
        }
        
    }

}
