package Observers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WindowEvents {
    @FunctionalInterface
    public interface BoolHandler {
        void handle(boolean value);
    }

    // List of handlers
    private static final List<BoolHandler> onFrameRendered = new CopyOnWriteArrayList<>();
    private static final List<BoolHandler> onWindowShutdown = new CopyOnWriteArrayList<>();

    public static void onFrameRendered(BoolHandler handler) { onFrameRendered.add(handler); }
    public static void onWindowShutdown(BoolHandler handler) { onWindowShutdown.add(handler); }

    public static void setFrameRenderedCallback(boolean value) { for (var h : onFrameRendered) { h.handle(value); }}
    public static void setWindowShutdownCallback(boolean value) { for (var h : onWindowShutdown) { h.handle(value); }}

}
