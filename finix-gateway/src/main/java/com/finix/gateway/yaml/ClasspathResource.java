package com.finix.gateway.yaml;
import static com.google.common.base.Throwables.propagate;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;



public class ClasspathResource implements Resource {
    public static final String CLASSPATH_SCHEME = "classpath:";

    private final String path;
    private final ClassLoader classLoader;

    /**
     * Construct using a path and the {@link ClassLoader} associated with a given class.
     *
     * @param path  path
     * @param clazz class to get {@link ClassLoader} from
     */
    public ClasspathResource(String path, Class<?> clazz) {
        this(path, clazz.getClassLoader());
    }

    /**
     * Construct using a path and class loader.
     *
     * @param path        path
     * @param classLoader class loader
     */
    public ClasspathResource(String path, ClassLoader classLoader) {
        this.path = stripInitialSlash(path.replace(CLASSPATH_SCHEME, ""));
        this.classLoader = classLoader;
    }

    private static String stripInitialSlash(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }

        return path;
    }

    @Override
    public String path() {
        return this.path;
    }

    @Override
    public URL url() {
        URL url = this.classLoader.getResource(this.path);

        if (url == null) {
            throw propagate(new FileNotFoundException(this.path));
        }

        return url;
    }

    @Override
    public String absolutePath() {
        return url().getFile();
    }

    @Override
    public InputStream inputStream() throws FileNotFoundException {
        InputStream stream = classLoader.getResourceAsStream(this.path);

        if (stream == null) {
            throw new FileNotFoundException(CLASSPATH_SCHEME + path);
        }

        return stream;
    }

    @Override
    public ClassLoader classLoader() {
        return classLoader;
    }

    @Override
    public String toString() {
        return CLASSPATH_SCHEME + path();
    }
}