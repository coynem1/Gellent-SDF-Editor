package Observers;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WindowEvents {
    @FunctionalInterface
    public interface BoolHandler {
        void handle(boolean value);
    }
    @FunctionalInterface
    public interface posHandler {
        void handle(int width, int height);
    }


    // List of handlers
    private static final List<BoolHandler> onFrameRendered = new CopyOnWriteArrayList<>();
    private static final List<posHandler> onScreenResized = new CopyOnWriteArrayList<>();

    public static void onFrameRendered(BoolHandler handler) { onFrameRendered.add(handler); }
    public static void onScreenResized(posHandler handler) { onScreenResized.add(handler); }

    public static void setFrameRenderedCallback(boolean value) { for (var h : onFrameRendered) { h.handle(value); }}
    public static void setScreenResizedCallback(int width, int height) { for (var h : onScreenResized) { h.handle(width, height); }}

}
