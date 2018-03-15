package com.finix.gateway.yaml;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Interface for a resource descriptor that abstracts from the actual type of underlying resource,
 * such as a file or class path resource.
 */
public interface Resource {
    /**
     * Path associated with this resource. This could be relative to a file system location, or classpath depending on the
     * resource type
     *
     * @return path
     */
    String path();

    /**
     * URL associated with this resource. The form of this URL will depend on the resource type.
     *
     * @return URL
     */
    URL url();

    /**
     * Absolute path associated with this resource. This could be relative to the file system, or classpath depending on the
     * resource type
     *
     * @return
     */
    String absolutePath();

    /**
     * Creates an {@link InputStream} that supplies the content of the resource.
     *
     * @return an input stream
     * @throws IOException if the stream cannot be created, e.g. if the resource does not exist
     */
    InputStream inputStream() throws IOException;

    /**
     * Returns the classloader if this is a classpath resource.
     *
     * @return classloader
     * @throws UnsupportedOperationException if this is not a classpath resource
     */
    ClassLoader classLoader() throws UnsupportedOperationException;
}