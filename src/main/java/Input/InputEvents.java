package Input;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.GLFW.*;


// TODO Delete
// Signals for input events
public final class InputEvents {
    @FunctionalInterface    // Single method interface
    public interface KeyHandler {
        void handle(int key, int scancode, int mods);
    }

    // List of key handlers
    private static final List<KeyHandler> onKeyPressed = new CopyOnWriteArrayList<>();
    private static final List<KeyHandler> onKeyReleased = new CopyOnWriteArrayList<>();

    private InputEvents() {}

    public static void onKeyPressed(KeyHandler handler) {
        onKeyPressed.add(handler);
    }

    public static void onKeyReleased(KeyHandler handler) {
        onKeyReleased.add(handler);
    }

    // Call this from your GLFW key callback
    public static void dispatchKey(int key, int scancode, int action, int mods) {
        // Evoke if inputs pressed or released
        if (action == GLFW_PRESS) {
            for (var h : onKeyPressed) h.handle(key, scancode, mods);
        } else if (action == GLFW_RELEASE) {
            for (var h : onKeyReleased) h.handle(key, scancode, mods);
        }
    }
}