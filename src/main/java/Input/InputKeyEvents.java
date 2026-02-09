package Input;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.GLFW.*;

// Signals for input events
public class InputKeyEvents {
    @FunctionalInterface    // Single method interface
    public interface KeyHandler {
        void handle(int key, int scancode, int mods);
    }

    // List of key handlers
    private static final List<KeyHandler> onKeyPressed = new CopyOnWriteArrayList<>();
    private static final List<KeyHandler> onKeyReleased = new CopyOnWriteArrayList<>();

    public static void onKeyPressed(KeyHandler handler) {
        onKeyPressed.add(handler);
    }

    public static void onKeyReleased(KeyHandler handler) {
        onKeyReleased.add(handler);
    }

    // Binds keys to a function call
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        // Evoke if inputs pressed or released
        switch (action) {
            case GLFW_PRESS:
                for (var h : onKeyPressed) {
                    h.handle(key, scancode, mods);
                }
                break;
            case GLFW_RELEASE:
                for (var h : onKeyReleased) {
                    h.handle(key, scancode, mods);
                }
                break;
        }
    }
}