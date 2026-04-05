package Observers;

import Input.InputShapes;
import Rendering.Objects.GameObject;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputShapesEvents {
    @FunctionalInterface    // Single method interface
    public interface ToolHandler {
        void handle(InputShapes.TOOLS tool);
    }
    @FunctionalInterface
    public interface GameObjectHandler {
        void handle(GameObject object);
    }

    // List of handlers
    private static final List<ToolHandler> onToolModeChanged = new CopyOnWriteArrayList<>();
    private static final List<GameObjectHandler> onSelectedChanged = new CopyOnWriteArrayList<>();

    public static void onToolModeChanged(ToolHandler handler) { onToolModeChanged.add(handler); }
    public static void onSelectedChanged(GameObjectHandler handler) { onSelectedChanged.add(handler); }

    // Update all colour change observers
    public static void setToolModeCallback(InputShapes.TOOLS toolMode) { for (var h : onToolModeChanged) { h.handle(toolMode); } }
    public static void setSelectedCallback(GameObject object) { for (var h : onSelectedChanged) { h.handle(object); } }

}
