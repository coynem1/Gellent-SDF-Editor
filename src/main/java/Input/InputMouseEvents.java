package Input;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.GLFW.*;

// Signals for input events
public class InputMouseEvents {
    @FunctionalInterface    // Single method interface
    public interface MouseBtnHandler {
        void handle(int button, int mods);
    }

    // List of key handlers
    private static final List<MouseBtnHandler> onMouseBtnPressed = new CopyOnWriteArrayList<>();
    private static final List<MouseBtnHandler> onMouseBtnReleased = new CopyOnWriteArrayList<>();

    public static void onBtnPressed(MouseBtnHandler handler) {
        onMouseBtnPressed.add(handler);
    }

    public static void onBtnReleased(MouseBtnHandler handler) {
        onMouseBtnReleased.add(handler);
    }

    // Binds mouse buttons to a function call
    public static void btnCallback(long window, int button, int action, int mods) {
        // Evoke if inputs pressed or released
        switch (action) {
            case GLFW_PRESS:
                for (var h : onMouseBtnPressed) {
                    h.handle(button, mods);
                }
                break;
            case GLFW_RELEASE:
                for (var h : onMouseBtnReleased) {
                    h.handle(button, mods);
                }
                break;
        }
    }
}