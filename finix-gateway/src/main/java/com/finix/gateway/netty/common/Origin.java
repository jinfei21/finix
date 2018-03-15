package com.finix.gateway.netty.common;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Objects;

import com.google.common.net.HostAndPort;

import lombok.Data;

@Data
public class Origin implements Comparable<Origin>{

    private final HostAndPort host;
	
    private final String domain;
	
    private final int hashCode;
        
    public static Builder newOriginBuilder(HostAndPort host) {
        return new Builder(host);
    }

    public static Builder newOriginBuilder(String host, int port) {
        return new Builder(HostAndPort.fromParts(host, port));
    }
    
    private Origin(Builder builder) {
        this.host = checkNotNull(builder.host);
        this.domain = builder.domain;

        this.hashCode = Objects.hash(this.domain, this.host);
    }
    
	public String id(){
		return host.toString();
	}
    
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Origin other = (Origin) obj;
        return Objects.equals(this.domain, other.domain)
                && Objects.equals(this.host, other.host);
    }

    @Override
    public String toString() {
        return format("%s:%s", domain, host);
    }

    @Override
    public int compareTo(Origin other) {
        return this.host.toString().compareTo(other.host.toString());
    }
	

    public static final class Builder {
        private final HostAndPort host;
    	
        private String domain;
        
        private Builder(HostAndPort host) {
            this.host = host;
        }

        private Builder(Origin origin) {
            this.host = origin.host;
            this.domain = origin.domain;
        }
        
        public Builder domain(String domain){
        	this.domain = domain;
        	return this;
        }
        public Origin build() {
            return new Origin(this);
        }
    }
}
