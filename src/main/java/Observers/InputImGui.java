package Observers;

import Input.InputShapes;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputImGui {
    @FunctionalInterface    // Single method interface
    public interface Vec3fHandler {
        void handle(Vector3f colour);
    }
    @FunctionalInterface
    public interface Vec2fHandler {
        void handle(Vector2f colour, boolean pressed);
    }
    @FunctionalInterface
    public interface FloatHandler {
        void handle(float value, boolean pressed);
    }
    @FunctionalInterface
    public interface ShapeHandler {
        void handle(InputShapes.SHAPES shape);
    }
    @FunctionalInterface
    public interface ToolHandler {
        void handle(InputShapes.TOOLS tool);
    }

    // List of handlers
    private static final List<Vec3fHandler> onColourChanged = new CopyOnWriteArrayList<>();
    private static final List<Vec2fHandler> onPosChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onRotationChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onScaleChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onBlendChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onRoundChanged = new CopyOnWriteArrayList<>();
    private static final List<ShapeHandler> onShapeChanged = new CopyOnWriteArrayList<>();
    private static final List<ToolHandler> onToolChanged = new CopyOnWriteArrayList<>();

    public static void onColourChanged(Vec3fHandler handler) { onColourChanged.add(handler); }
    public static void onPosChanged(Vec2fHandler handler) { onPosChanged.add(handler); }
    public static void onRotationChanged(FloatHandler handler) { onRotationChanged.add(handler); }
    public static void onScaleChanged(FloatHandler handler) { onScaleChanged.add(handler); }
    public static void onRoundChanged(FloatHandler handler) { onRoundChanged.add(handler); }
    public static void onBlendChanged(FloatHandler handler) { onBlendChanged.add(handler); }
    public static void onShapeChanged(ShapeHandler handler) { onShapeChanged.add(handler); }
    public static void onToolChanged(ToolHandler handler) { onToolChanged.add(handler); }

    // Update all colour change observers
    public static void setColourCallback(Vector3f colour) {
        for (var h : onColourChanged) { h.handle(colour); }
    }
    public static void setPosCallback(Vector2f position, boolean pressed) {
        for (var h : onPosChanged) { h.handle(position, pressed); }
    }
    public static void setRotationCallback(float rotation, boolean pressed) {
        for (var h : onRotationChanged) { h.handle(rotation, pressed); }
    }
    public static void setScaleCallback(float scale, boolean pressed) {
        for (var h : onScaleChanged) { h.handle(scale, pressed); }
    }
    public static void setBlendCallback(float blend, boolean pressed) { for (var h : onBlendChanged) { h.handle(blend, pressed); }}
    public static void setRoundCallback(float blend, boolean pressed) { for (var h : onRoundChanged) { h.handle(blend, pressed); }}
    public static void setShapeCallback(InputShapes.SHAPES shape) { for (var h : onShapeChanged) { h.handle(shape); }}
    public static void setToolCallback(InputShapes.TOOLS tool) { for (var h : onToolChanged) { h.handle(tool); }}
}
