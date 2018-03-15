package com.finix.gateway.config;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

public class Certificate {
    private String alias;
    private String certificatePath;

    @JsonCreator
    Certificate(@JsonProperty("alias") String alias,
                @JsonProperty("path") String certificatePath) {
        this.alias = checkNotNull(alias);
        this.certificatePath = checkNotNull(certificatePath);
    }

    public static Certificate certificate(String alias, String certificatePath) {
        return new Builder()
                .setAlias(alias)
                .setCertificatePath(certificatePath)
                .build();
    }

    private Certificate(Builder builder) {
        this.alias = builder.alias;
        this.certificatePath = builder.certificatePath;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getCertificatePath() {
        return this.certificatePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Certificate that = (Certificate) o;

        if (alias != null ? !alias.equals(that.alias) : that.alias != null) {
            return false;
        }
        return certificatePath != null ? certificatePath.equals(that.certificatePath) : that.certificatePath == null;

    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + (certificatePath != null ? certificatePath.hashCode() : 0);
        return result;
    }

    /**
     * certificate builder.
     */
    public static final class Builder {
        private String alias;
        private String certificatePath;

        private Builder() {
        }

        /**
         * set alias.
         * @param alias alias
         * @return this
         */
        public Certificate.Builder setAlias(String alias) {
            this.alias = alias;
            return this;
        }

        /**
         * set certificate path.
         * @param certificatePath certificate path
         * @return this
         */
        public Certificate.Builder setCertificatePath(String certificatePath) {
            this.certificatePath = certificatePath;
            return this;
        }

        /**
         * build certificate.
         * @return new certificate
         */
        public Certificate build() {
            return new Certificate(this);
        }
    }
}