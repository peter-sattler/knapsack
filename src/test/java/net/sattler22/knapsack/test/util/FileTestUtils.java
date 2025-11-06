package net.sattler22.knapsack.test.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * File Test Utilities
 *
 * @author Pete Sattler
 * @version November 2025
 */
public final class FileTestUtils {

    private FileTestUtils() {
        throw new AssertionError("Class cannot be instantiated");
    }

    /**
     * Read a resource as a <code>String</code>
     *
     * @param path The resource's relative path
     * @return The resource contents
     * @throws IOException If unable tp read the resource
     * @throws URISyntaxException If unable tp read the resource
     */
    public static String readResourceAsString(String path) throws IOException, URISyntaxException {
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path is required");
        final URL url = FileTestUtils.class.getResource(path);
        if (url == null)
            throw new FileNotFoundException(path + " not found");
        return Files.readString(Paths.get(url.toURI()));
    }
}
