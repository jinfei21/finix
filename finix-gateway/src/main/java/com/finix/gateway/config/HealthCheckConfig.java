package com.finix.gateway.config;

import static com.google.common.base.Objects.toStringHelper;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finix.gateway.common.Constants;

import lombok.Data;

@Data
public class HealthCheckConfig {

    @JsonProperty("uri")
	private final String uri;
    
    @JsonProperty("intervalMillis")
	private final long intervalMillis;
    
    @JsonProperty("timeoutMillis")
	private final long timeoutMillis;
    
    @JsonProperty("healthyThreshold")
	private final long healthyThreshold;
    
    @JsonProperty("unhealthyThreshold")
	private final long unhealthyThreshold;
    
    
    public HealthCheckConfig(){
        this.uri = "/hs";
        this.intervalMillis = Constants.DEFAULT_HEALTH_CHECK_INTERVAL;
        this.timeoutMillis = Constants.DEFAULT_TIMEOUT_VALUE;
        this.healthyThreshold = Constants.DEFAULT_HEALTHY_THRESHOLD_VALUE;
        this.unhealthyThreshold = Constants.DEFAULT_UNHEALTHY_THRESHOLD_VALUE;
    }
	
    public HealthCheckConfig(String uri,long intervalMillis,long timeoutMillis,long healthyThreshold,long unhealthyThreshold){
    	this.uri = uri;
    	this.intervalMillis = intervalMillis;
    	this.timeoutMillis = timeoutMillis;
    	this.healthyThreshold = healthyThreshold;
    	this.unhealthyThreshold = unhealthyThreshold;
    }
    
    public static HealthCheckConfig noHealthCheck() {
        return new HealthCheckConfig();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.uri, this.intervalMillis, this.timeoutMillis, this.healthyThreshold, this.unhealthyThreshold);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HealthCheckConfig other = (HealthCheckConfig) obj;
        return Objects.equals(this.uri, other.uri)
                && Objects.equals(this.intervalMillis, other.intervalMillis)
                && Objects.equals(this.timeoutMillis, other.timeoutMillis)
                && Objects.equals(this.healthyThreshold, other.healthyThreshold)
                && Objects.equals(this.unhealthyThreshold, other.unhealthyThreshold);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("uri", this.uri)
                .add("intervalMillis", this.intervalMillis)
                .add("timeoutMillis", this.timeoutMillis)
                .add("healthyThreshold", this.healthyThreshold)
                .add("unhealthyThreshold", this.unhealthyThreshold)
                .toString();
    }		
}
