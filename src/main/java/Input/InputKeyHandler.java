package Input;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class InputKeyHandler {
    @FunctionalInterface    // Single method interface
    public interface KeyHandler {
        void handle(int key, int scancode, int mods);
    }

    // List of key handlers
    protected static final List<InputKeyEvents.KeyHandler> onKeyPressed = new CopyOnWriteArrayList<>();
    protected static final List<InputKeyEvents.KeyHandler> onKeyReleased = new CopyOnWriteArrayList<>();

    public static void onKeyPressed(InputKeyEvents.KeyHandler handler) {
        onKeyPressed.add(handler);
    }

    public static void onKeyReleased(InputKeyEvents.KeyHandler handler) {
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
