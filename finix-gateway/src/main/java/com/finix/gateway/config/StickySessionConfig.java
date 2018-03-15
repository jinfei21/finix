package com.finix.gateway.config;

import static com.google.common.base.Objects.toStringHelper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.finix.gateway.common.Constants;
import com.google.common.base.Objects;

import lombok.Data;

@Data
public class StickySessionConfig {


    @JsonProperty("enabled")
    private final boolean enabled;
    @JsonProperty("timeoutSeconds")
    private final long timeoutSeconds;
    
    private StickySessionConfig() {
        this(false, Constants.DEFAULT_SESSION_TIMEOUT);
    }

    @JsonCreator
    StickySessionConfig(@JsonProperty("enabled") boolean enabled,
                        @JsonProperty("timeoutSeconds") long timeoutSeconds) {
        this.enabled = enabled;
        this.timeoutSeconds = Optional.ofNullable(timeoutSeconds).orElse(Constants.DEFAULT_SESSION_TIMEOUT);
    }
    
    public static StickySessionConfig stickySessionDisabled() {
        return new StickySessionConfig();
    }
    

    private StickySessionConfig(Builder builder) {
        this(builder.enabled, builder.timeoutSeconds);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("enabled", enabled)
                .add("timeoutSeconds", timeoutSeconds)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.enabled, this.timeoutSeconds);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        StickySessionConfig that = (StickySessionConfig) obj;

        return this.enabled == that.enabled
                && this.timeoutSeconds == that.timeoutSeconds;
    }
    
    /**
     * A builder for the {StickySessionConfig}.
     */
    public static final class Builder {
        private boolean enabled;
        private long timeoutSeconds = Constants.DEFAULT_SESSION_TIMEOUT;

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder timeout(int timeout, TimeUnit timeUnit) {
            this.timeoutSeconds = (int) timeUnit.toSeconds(timeout);
            return this;
        }

        public StickySessionConfig build() {
            return new StickySessionConfig(this);
        }
    }
}
