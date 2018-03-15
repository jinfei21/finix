package com.finix.gateway.yaml;

import static com.google.common.base.Throwables.propagate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Resource implementation for {@link File} handles.
 */
public class FileResource implements Resource {
    public static final String FILE_SCHEME = "file:";

    private final File root;
    private final File file;

    public FileResource(File file) {
        this(file, file);
    }

    public FileResource(File root, File file) {
        this.root = root;
        this.file = file;
        if (!file.getAbsolutePath().startsWith(root.getAbsolutePath())) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " is not a parent of " + root.getAbsolutePath());
        }
    }

    public FileResource(String path) {
        this(new File(path.replace(FILE_SCHEME, "")));
    }

    @Override
    public String path() {
        if (file.equals(root)) {
            return file.getPath();
        }
        return file.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);
    }

    @Override
    public URL url() {
        try {
            return getFile().toURI().toURL();
        } catch (MalformedURLException e) {
            throw propagate(e);
        }
    }

    @Override
    public String absolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public InputStream inputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public ClassLoader classLoader() {
        throw new UnsupportedOperationException();
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return absolutePath();
    }
}
