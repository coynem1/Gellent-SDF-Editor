package Observers;

import Input.InputShapes;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputShapesEvents {
    @FunctionalInterface    // Single method interface
    public interface ToolHandler {
        void handle(InputShapes.TOOLS tool);
    }

    // List of handlers
    private static final List<ToolHandler> onToolModeChanged = new CopyOnWriteArrayList<>();

    public static void onToolModeChanged(ToolHandler handler) { onToolModeChanged.add(handler); }

    // Update all colour change observers
    public static void setToolModeCallback(InputShapes.TOOLS toolMode) { for (var h : onToolModeChanged) { h.handle(toolMode); } }
}
