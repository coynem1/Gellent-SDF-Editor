package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

// Loads files from the resources folder
public class ResourcesLoader {
    public static ByteBuffer load(String path) {
        try {
            InputStream in = ResourcesLoader.class.getClassLoader().getResourceAsStream(path);
            if (in == null) throw new IOException("Resource not found: " + path);

            byte[] bytes = in.readAllBytes();
            return ByteBuffer.allocateDirect(bytes.length).put(bytes).flip();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + path, e);
        }
    }

    public static byte[] loadBytes(String path) {
        try {
            InputStream in = ResourcesLoader.class.getClassLoader().getResourceAsStream(path);
            if (in == null) throw new IOException("Resource not found: " + path);

            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + path, e);
        }
    }
}
