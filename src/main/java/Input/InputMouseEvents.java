package Input;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.GLFW.*;

// Signals for input events
public class InputMouseEvents {
    @FunctionalInterface    // Single method interface
    public interface MouseBtnHandler {
        void handle(int button, int mods);
    }
    @FunctionalInterface    // Single method interface
    public interface MouseScrollHandler {
        void handle(double xOffset, double yOffset);
    }
    @FunctionalInterface    // Single method interface
    public interface MouseMoveHandler {
        void handle(int xPos, int yPos, int lastX, int lastY);
    }

    private static int lastMouseX = 0;
    private static int lastMouseY = 0;

    // List of key handlers
    private static final List<MouseBtnHandler> onMouseBtnPressed = new CopyOnWriteArrayList<>();
    private static final List<MouseBtnHandler> onMouseBtnReleased = new CopyOnWriteArrayList<>();
    private static final List<MouseScrollHandler> onMouseScrolled = new CopyOnWriteArrayList<>();
    private static final List<MouseMoveHandler> onMouseMoved = new CopyOnWriteArrayList<>();

    public static void onBtnPressed(MouseBtnHandler handler) {
        onMouseBtnPressed.add(handler);
    }

    public static void onBtnReleased(MouseBtnHandler handler) {
        onMouseBtnReleased.add(handler);
    }

    public static void onScroll(MouseScrollHandler handler) {
        onMouseScrolled.add(handler);
    }

    public static void onMove(MouseMoveHandler handler) { onMouseMoved.add(handler); }

    // Callback for mouse buttons is directed to function calls
    public static void btnMouseCallback(long window, int button, int action, int mods) {
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

    // Callback for mouse scrolling is directed to function calls
    public static void scrollMouseCallback(long window, double xOffset, double yOffset) {
        for (var h : onMouseScrolled) {
            h.handle(xOffset, yOffset);
        }
    }

    // Callback for mouse moving is directed to function calls
    public static void moveMouseCallback(long window, double xPos, double yPos) {
        int xPosi = (int) xPos + 1;
        int yPosi = (int) yPos + 1;

        for (var h : onMouseMoved) {
            h.handle(xPosi, yPosi, lastMouseX, lastMouseY);
        }
        lastMouseX = xPosi;
        lastMouseY = yPosi;
    }

    public static Vector2i getMousePos() {
        return new Vector2i(lastMouseX, lastMouseY);
    }
}