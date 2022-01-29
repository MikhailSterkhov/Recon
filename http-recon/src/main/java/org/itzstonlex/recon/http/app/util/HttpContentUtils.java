package org.itzstonlex.recon.http.app.util;

import org.itzstonlex.recon.http.app.HttpContextHandler;
import org.itzstonlex.recon.http.app.HttpContextPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public final class HttpContentUtils {

    public static InputStream getInputStream(Class<?> classLoader, PathLevel pathLevel, String filePath) {
        switch (pathLevel) {
            case FILESYSTEM: {
                try {
                    File file = Paths.get(filePath).toFile();
                    return new FileInputStream(file);
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }
                break;
            }

            case CLASSPATH: {
                return classLoader.getResourceAsStream(filePath);
            }
        }

        return null;
    }

    public static void initContextContent(HttpContextPath httpContextPath, HttpContextHandler httpContextHandler) {
        PathLevel pathLevel = httpContextPath.level();

        InputStream contentStream = HttpContentUtils.getInputStream(httpContextHandler.getClass(), pathLevel,
                (httpContextPath.baseDir() + httpContextPath.contentPath()).replace("//", "/"));

        if (contentStream == null) {
            contentStream = HttpContentUtils.getInputStream(httpContextHandler.getClass(), pathLevel,
                    (httpContextPath.baseDir() + httpContextPath.context().concat(".html")).replace("//", "/"));
        }

        httpContextHandler.setContentStream(httpContextPath.context(), contentStream);
    }

}
