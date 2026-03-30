package Observers;

import Input.InputShapes;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputImGui {
    @FunctionalInterface    // Single method interface
    public interface Vec3fHandler {
        void handle(Vector3f colour);
    }
    @FunctionalInterface
    public interface FloatHandler {
        void handle(float value);
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
    private static final List<Vec3fHandler> onPosChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onRotationChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onScaleChanged = new CopyOnWriteArrayList<>();
    private static final List<FloatHandler> onBlendChanged = new CopyOnWriteArrayList<>();
    private static final List<ShapeHandler> onShapeChanged = new CopyOnWriteArrayList<>();
    private static final List<ToolHandler> onToolChanged = new CopyOnWriteArrayList<>();

    public static void onColourChanged(Vec3fHandler handler) { onColourChanged.add(handler); }
    public static void onPosChanged(Vec3fHandler handler) { onPosChanged.add(handler); }
    public static void onRotationChanged(FloatHandler handler) { onRotationChanged.add(handler); }
    public static void onScaleChanged(FloatHandler handler) { onScaleChanged.add(handler); }
    public static void onBlendChanged(FloatHandler handler) { onBlendChanged.add(handler); }
    public static void onShapeChanged(ShapeHandler handler) { onShapeChanged.add(handler); }
    public static void onToolChanged(ToolHandler handler) { onToolChanged.add(handler); }

    // Update all colour change observers
    public static void setColourCallback(Vector3f colour) {
        for (var h : onColourChanged) { h.handle(colour); }
    }
    public static void setPosCallback(Vector3f position) {
        for (var h : onPosChanged) { h.handle(position); }
    }
    public static void setRotationCallback(float rotation) {
        for (var h : onRotationChanged) { h.handle(rotation); }
    }
    public static void setScaleCallback(float scale) {
        for (var h : onScaleChanged) { h.handle(scale); }
    }
    public static void setBlendCallback(float blend) { for (var h : onBlendChanged) { h.handle(blend); }}
    public static void setShapeCallback(InputShapes.SHAPES shape) { for (var h : onShapeChanged) { h.handle(shape); }}
    public static void setToolCallback(InputShapes.TOOLS tool) { for (var h : onToolChanged) { h.handle(tool); }}
}
