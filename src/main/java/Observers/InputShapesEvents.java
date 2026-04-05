package Observers;

import Input.InputShapes;
import Rendering.Objects.GameObject;
import org.joml.Vector2f;
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
    @FunctionalInterface
    public interface FloatHandler {
        void handle(float value);
    }
    @FunctionalInterface
    public interface Vec2Handler {
        void handle(Vector2f vec);
    }

    // List of handlers
    private static final List<ToolHandler> onToolModeChanged = new CopyOnWriteArrayList<>();
    private static final List<GameObjectHandler> onSelectedChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onBlendChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onRoundChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onScaleChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onRotationChanged = new CopyOnWriteArrayList<>();
    private static final List<Vec2Handler> onPosChanged = new CopyOnWriteArrayList<>();

    public static void onToolModeChanged(ToolHandler handler) { onToolModeChanged.add(handler); }
    public static void onSelectedChanged(GameObjectHandler handler) { onSelectedChanged.add(handler); }
    public static void onBlendChanged(FloatHandler handler) { onBlendChanged.add(handler); }
    public static void onRoundChanged(FloatHandler handler) { onRoundChanged.add(handler); }
    public static void onScaleChanged(FloatHandler handler) { onScaleChanged.add(handler); }
    public static void onRotationChanged(FloatHandler handler) { onRotationChanged.add(handler); }
    public static void onPosChanged(Vec2Handler handler) { onPosChanged.add(handler); }

    // Update all colour change observers
    public static void setToolModeCallback(InputShapes.TOOLS toolMode) { for (var h : onToolModeChanged) { h.handle(toolMode); } }
    public static void setSelectedCallback(GameObject object) { for (var h : onSelectedChanged) { h.handle(object); } }
    public static void setBlendCallback(float blend) { for (var h : onBlendChanged) { h.handle(blend); } }
    public static void setRoundCallback(float round) { for (var h : onRoundChanged) { h.handle(round); } }
    public static void setScaleCallback(float scale) { for (var h : onScaleChanged) { h.handle(scale); } }
    public static void setRotationCallback(float rotation) { for (var h : onRotationChanged) { h.handle(rotation); } }
    public static void setPosCallback(Vector2f vec) { for (var h : onPosChanged) { h.handle(vec); } }

}
