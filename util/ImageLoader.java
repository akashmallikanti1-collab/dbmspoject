package util;

import javafx.scene.image.Image;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Centralized image loader.
 * Images must be placed inside the resources/ folder on the classpath.
 * The image_url column in car_data stores just the filename, e.g. "creta.jpg"
 */
public class ImageLoader {

    // Fallback image served from Wikipedia (always reachable)
    private static final String FALLBACK_URL =
        "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/No-Image-Placeholder.svg/800px-No-Image-Placeholder.svg.png";

    /**
     * Load a car image by filename (e.g. "creta.jpg").
     * Looks for the file in the resources/ folder on the classpath or filesystem.
     */
    public static Image load(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return fallback();
        }

        // Try classpath resource first
        try {
            var stream = ImageLoader.class.getResourceAsStream("/resources/" + fileName);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception ignored) {}

        // Try classpath without resources prefix
        try {
            var stream = ImageLoader.class.getResourceAsStream(fileName);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception ignored) {}

        // Try resource URL loading
        try {
            var resource = ImageLoader.class.getResource("/resources/" + fileName);
            if (resource != null) {
                return new Image(resource.toString(), true);
            }
            resource = ImageLoader.class.getResource(fileName);
            if (resource != null) {
                return new Image(resource.toString(), true);
            }
        } catch (Exception ignored) {}

        // Try filesystem path relative to the current working directory
        try {
            Path path = Paths.get(System.getProperty("user.dir"), "resources", fileName);
            if (Files.exists(path)) {
                return new Image(path.toUri().toString(), true);
            }
            path = Paths.get(System.getProperty("user.dir"), "fixed", "resources", fileName);
            if (Files.exists(path)) {
                return new Image(path.toUri().toString(), true);
            }
        } catch (Exception ignored) {}

        // Try URL directly for absolute links stored in DB
        try {
            if (fileName.startsWith("http")) {
                return new Image(fileName, true);
            }
        } catch (Exception ignored) {}

        return fallback();
    }

    public static Image fallback() {
        try {
            return new Image(FALLBACK_URL, true);
        } catch (Exception e) {
            return new Image("data:image/png;base64,"); // empty
        }
    }
}
