package com.finix.gateway.yaml;


import static com.finix.gateway.yaml.ClasspathResource.CLASSPATH_SCHEME;
/**
 * Factory for creating resources (file, classpath) based on prefix.
 */
public class ResourceFactory {

    /**
     * Create a new resource from {@code path} using the {@code classLoader}.
     *
     * @param path a resource path
     * @param classLoader the classloader to load it with (if it is a classpath resource)
     * @return a resource
     */
    public static Resource newResource(String path, ClassLoader classLoader) {
        if (path.startsWith(CLASSPATH_SCHEME)) {
            return new ClasspathResource(path, classLoader);
        }

        return new FileResource(path);
    }

    /**
     * Create a new resource from {@code path} using the current thread classloader.
     *
     * @param path a resource path
     * @return a resource
     */
    public static Resource newResource(String path) {
        return newResource(path, Thread.currentThread().getContextClassLoader());
    }
}