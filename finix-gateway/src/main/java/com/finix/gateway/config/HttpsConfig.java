package com.finix.gateway.config;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Strings.isNullOrEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import lombok.Data;

@Data
public class HttpsConfig {

	private final int port;
	private final String sslProvider;
	private final String certFile;
	private final String certKeyFile;
	private final long sessionTimeoutMs;
	private final long sessionCacheSize;
	
	
	public HttpsConfig(@JsonProperty("port") int port ,
			@JsonProperty("sslProvider") String sslProvider
			,@JsonProperty("certFile") String certFile
			,@JsonProperty("certKeyFile") String certKeyFile,
			@JsonProperty("sessionTimeoutMs") long sessionTimeoutMs,
			@JsonProperty("sessionCacheSize") long sessionCacheSize ){
		this.port = port;
		this.sslProvider = sslProvider;
		this.certFile = certFile;
		this.certKeyFile = certKeyFile;
		this.sessionTimeoutMs = sessionTimeoutMs;
		this.sessionCacheSize = sessionCacheSize;
	}
	
	
    public String type() {
        return "https";
    }
    

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hashCode(sslProvider, certFile, certKeyFile, sessionTimeoutMs, sessionCacheSize);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        HttpsConfig other = (HttpsConfig) obj;
        return Objects.equal(this.sslProvider, other.sslProvider)
                && Objects.equal(this.certFile, other.certFile)
                && Objects.equal(this.certKeyFile, other.certKeyFile)
                && Objects.equal(this.sessionTimeoutMs, other.sessionTimeoutMs)
                && Objects.equal(this.sessionCacheSize, other.sessionCacheSize);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("port", port)
                .add("sslProvider", sslProvider)
                .add("certificateFile", certFile)
                .add("certificateKeyFile", certKeyFile)
                .add("sessionTimeoutMillis", sessionTimeoutMs)
                .add("sessionCacheSize", sessionCacheSize)
                .toString();
    }

    public boolean isConfigured() {
        return !isNullOrEmpty(certFile) && !isNullOrEmpty(certKeyFile);
    }
	
}
