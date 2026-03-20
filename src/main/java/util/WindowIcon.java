package util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public abstract class WindowIcon {
    private final static int RGBA_CHANNELS = 4;
    private final static String ICON_PATH = "assets/Images/icon.png";

    public static void setWindowIcon(long window) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Load RGBA pixel data
            ByteBuffer pixels = STBImage.stbi_load(ICON_PATH, w, h, channels, RGBA_CHANNELS);
            if (pixels == null) {
                System.err.println("Failed to load icon: " + STBImage.stbi_failure_reason());
                return;
            }

            // Wrap in a GLFWImage buffer and assign
            GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1, stack);
            iconBuffer.position(0)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels);

            GLFW.glfwSetWindowIcon(window, iconBuffer);

            // Free pixel data
            STBImage.stbi_image_free(pixels);
        }
    }
}
