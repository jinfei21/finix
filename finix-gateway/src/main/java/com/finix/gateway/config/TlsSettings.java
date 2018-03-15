package com.finix.gateway.config;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptySet;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.finix.gateway.common.Constants;
import com.finix.gateway.util.StringUtil;
import com.google.common.collect.Sets;

import lombok.Data;

@Data
@JsonDeserialize(builder = TlsSettings.Builder.class)
public class TlsSettings {

    private static final String DEFAULT_TRUST_STORE_PATH = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
    
    @JsonProperty("trustAllCerts")
    private final boolean trustAllCerts;
    @JsonProperty("sslProvider")
    private final String sslProvider;
    @JsonProperty("addlCerts")
    private final Set<Certificate> additionalCerts;
    @JsonProperty("trustStorePath")
    private final String trustStorePath;
    @JsonProperty("trustStorePassword")
    private final char[] trustStorePassword;
    
    private TlsSettings(Builder builder) {
        this.trustAllCerts = checkNotNull(builder.trustAllCerts);
        this.sslProvider = checkNotNull(builder.sslProvider);
        this.additionalCerts = builder.additionalCerts;
        this.trustStorePath = builder.trustStorePath;
        this.trustStorePassword = StringUtil.toCharArray(builder.trustStorePassword);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TlsSettings other = (TlsSettings) obj;
        return Objects.equals(this.trustAllCerts, other.trustAllCerts)
                && Objects.equals(this.sslProvider, other.sslProvider)
                && Objects.equals(this.additionalCerts, other.additionalCerts)
                && Objects.equals(this.trustStorePath, other.trustStorePath)
                && Arrays.equals(this.trustStorePassword, other.trustStorePassword);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("trustAllCerts", this.trustAllCerts)
                .add("sslProvider", this.sslProvider)
                .add("additionalCerts", this.additionalCerts)
                .add("trustStorePath", this.trustStorePath)
                .add("trustStorePassword", this.trustStorePassword)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(trustAllCerts, sslProvider, additionalCerts,
                trustStorePath, trustStorePassword);
    }
    
    /**
     * The builder for SSL settings.
     */
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
    public static final class Builder {
        private boolean trustAllCerts = true;
        private String sslProvider = Constants.DEFAULT_SSL_PROVIDER;
        private Set<Certificate> additionalCerts = emptySet();
        private String trustStorePath = firstNonNull(System.getProperty("javax.net.ssl.trustStore"),DEFAULT_TRUST_STORE_PATH);
        private String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        
        /**
         * @deprecated
         * Skips origin authentication.
         *
         * When true, styx will not attempt to authenticate backend servers.
         * It will accept any certificate presented by the origins.
         *
         * @param trustAllCerts
         * @return
         */
        @JsonProperty("trustAllCerts")
        @Deprecated
        public Builder trustAllCerts(boolean trustAllCerts) {
            this.trustAllCerts = trustAllCerts;
            return this;
        }

        @JsonProperty("authenticate")
        public Builder authenticate(boolean authenticate) {
            this.trustAllCerts = !authenticate;
            return this;
        }

        /**
         * Sets SSL provider.
         *
         * @param sslProvider
         * @return
         */
        @JsonProperty("sslProvider")
        public Builder sslProvider(String sslProvider) {
            this.sslProvider = sslProvider;
            return this;
        }

        /**
         * Configures additional certificates.
         *
         * The additional certificates are loaded into the java keystore that has been
         * initialised from the trust store file.
         *
         * @param certificates
         * @return
         */
        @JsonProperty("addlCerts")
        public Builder additionalCerts(Certificate... certificates) {
            this.additionalCerts = Sets.newHashSet(certificates);
            return this;
        }

        /**
         * A path to trust store that is used to verify credentials presented by
         * remote origin.
         *
         * @param trustStorePath
         * @return
         */
        @JsonProperty("trustStorePath")
        public Builder trustStorePath(String trustStorePath) {
            this.trustStorePath = trustStorePath;
            return this;
        }

        @JsonProperty("trustStorePassword")
        public Builder trustStorePassword(String trustStorePwd) {
            this.trustStorePassword = trustStorePwd;
            return this;
        }

        public TlsSettings build() {
            if (!trustAllCerts && trustStorePassword == null) {
                throw new IllegalArgumentException("trustStorePassword must be supplied when remote peer authentication is enabled.");
            }
            return new TlsSettings(this);
        }
        
    }
}