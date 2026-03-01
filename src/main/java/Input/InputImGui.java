package Input;

import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputImGui {
    @FunctionalInterface    // Single method interface
    public interface ColourHandler {
        void handle(Vector3f colour);
    }

    // List of handlers
    private static final List<ColourHandler> onColourChanged = new CopyOnWriteArrayList<>();

    public static void onColourChanged(ColourHandler handler) {
        onColourChanged.add(handler);
    }

    // Update all colour change observers
    public static void setColourCallback(Vector3f colour) {
        for (var h : onColourChanged) { h.handle(colour); }
    }
}
