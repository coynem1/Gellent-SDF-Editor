package Input;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Signals for window events
public class InputWindowEvents {
    @FunctionalInterface    // Single method interface
    public interface WindowResizeHandler {
        void handle(int newWidth, int newHeight);
    }

    private static final List<WindowResizeHandler> onWindowRescaled = new CopyOnWriteArrayList<>();

    public static void onWindowResized(WindowResizeHandler handler) { onWindowRescaled.add(handler); }

    // Callback for window resize is directed to function calls
    public static void windowResizeCallback(long window, int newWidth, int newHeight) {
        onWindowRescaled.forEach(handler -> handler.handle(newWidth, newHeight));
    }
}
