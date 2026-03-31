package Input;

import Input.Actions.ActionHandler;
import Input.Actions.ActionStamp;
import Jade.Camera;
import Jade.Scene;
import Jade.SceneManager;
import Observers.InputImGui;
import Observers.InputKeyEvents;
import Observers.InputMouseEvents;
import Observers.InputShapesEvents;
import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.Components.Blending;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GameObject;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Transform2D;
import util.WorldCoords;

import static org.lwjgl.glfw.GLFW.*;

public class InputStampShapes {
    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private Blending blending = new Blending();
    private SculptObject currentSculpt = new SculptObject();
    private Shape activeShape = null;

    private SceneManager sceneManager;
    private ActionHandler actionHandler;
    private InputShapes inputShapes;

    private Vector2f mousePos = new Vector2f();
    private boolean shortcutsUsed[] = new boolean[InputShapes.SHORTCUTS.values().length];
    private boolean activeTransform = true; // Active shape tracks mouse position
    private InputShapes.SHAPES selectedShape = InputShapes.SHAPES.CIRCLE;
    private InputShapes.MODES stampMode = InputShapes.MODES.UNION;
    private boolean enabled = false;

    public InputStampShapes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        transform.setPosition(new Vector2f(0f, 0f));
    }

    public void init(InputShapes inputShapes) {
        this.actionHandler = sceneManager.getActionHandler();
        this.inputShapes = inputShapes;

        updateActiveShape();
        bindInputs();
    }

    private void bindInputs() {
        // ImGui UI inputs
        InputImGui.onColourChanged((colour) -> {
            colourSelected = colour;
            if (!enabled) return;
            if (activeShape != null) activeShape.setColour(colour);
        });
        InputImGui.onScaleChanged((scale) -> {
            transform.setScale(scale);
            if (!enabled) return;
            if (activeShape != null) activeShape.setTransform(transform);
        });
        InputImGui.onBlendChanged((blend) -> {
            blending.setBlend(blend);
            if (!enabled) return;
            if (activeShape != null) activeShape.setBlend(blend);
        });
        InputImGui.onRotationChanged((rotation) -> {
            // Convert to radians
            rotation *= (float) Math.PI / 180;
            transform.setRotation(-rotation);
            if (!enabled) return;
            if (activeShape != null) activeShape.setTransform(transform);
        });
        InputImGui.onShapeChanged((shape) -> {
            selectedShape = shape;
            if (!enabled) return;
            if (activeShape != null) activeShape.setShape(shape);
        });
        InputImGui.onToolChanged((tool) -> {
            updateActiveShape();
        });
        InputShapesEvents.onToolModeChanged((tool) -> {
            updateActiveShape();
        });

        InputMouseEvents.onBtnPressed((button, _) -> {
            // If focused on UI, ignore
            if (ImGui.getIO().getWantCaptureMouse()) { return; }
            if (!enabled) return;

            switch (button) {
                // Change Scene
                case GLFW_MOUSE_BUTTON_LEFT:
                    stampShape();
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            // If focused on UI or not enabled, ignore
            if (ImGui.getIO().getWantCaptureMouse() || !enabled) return;

            mousePos = new Vector2f(xPos, yPos);
            Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());

            // Active shape changing
            if (!activeTransform) {
                shapeShortcut();
                return;
            }

            transform.setPosition(worldPos);
            if (activeShape != null) activeShape.setTransform(transform);
        });

        // Shortcuts
        InputKeyEvents.onKeyPressed((key, _, mods) -> {
            boolean ctrl  = (mods & GLFW_MOD_CONTROL) != 0;
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
            GameObject object = activeShape;
            boolean pressed = true;

            if (!enabled) return;

            if (!ctrl && !shift) {
                switch (key) {
                    case GLFW_KEY_BACKSPACE:
                        if (activeShape != null) stampMode = inputShapes.toggleMode(activeShape);
                        break;
                    case GLFW_KEY_S:
                        if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.SCALE)) object = inputShapes.scaleShortcut(object);
                        break;
                    case GLFW_KEY_R:
                        if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROTATE)) object = inputShapes.rotateShortcut(object);
                        break;
                    case GLFW_KEY_B:
                        if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.BLEND)) object = inputShapes.blendShortcut(object);
                        break;
                    case GLFW_KEY_F:
                        if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROUND)) object = inputShapes.roundShortcut(object);
                        break;
                }
            }

            activeShape = (Shape) object;
        });

        // Let go of a key
        InputKeyEvents.onKeyReleased((key, _, _) -> {
            boolean pressed = false;
            if (!enabled) return;

            Transform2D<Vector2f> transformShape = activeShape.getComponent(Transform2D.class);

            switch (key) {
                case GLFW_KEY_S:
                    freezeActiveShape(pressed, InputShapes.SHORTCUTS.SCALE);
                    if (transformShape != null) transform.setScale(transformShape.getScale());
                    break;
                case GLFW_KEY_R:
                    freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROTATE);
                    if (transformShape != null) transform.setRotation(transformShape.getRotation());
                    break;
                case GLFW_KEY_B:
                    freezeActiveShape(pressed, InputShapes.SHORTCUTS.BLEND);
                    break;
                case GLFW_KEY_F:
                    freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROUND);
                    break;
            }
        });
    }

    // Shortcuts for active shape
    private void shapeShortcut() {
        GameObject object = activeShape;

        if (shortcutsUsed[InputShapes.SHORTCUTS.ROTATE.ordinal()]) { object = inputShapes.rotateShortcut(object); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.SCALE.ordinal()]) { object = inputShapes.scaleShortcut(object); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.BLEND.ordinal()]) { object = inputShapes.blendShortcut(object); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.ROUND.ordinal()]) { object = inputShapes.roundShortcut(object); }

        activeShape = (Shape) object;
    }

    // Stops active shape from changing position
    private boolean freezeActiveShape(boolean pressed, InputShapes.SHORTCUTS shortcut) {
        if (activeShape == null) return false;

        // Shortcut array
        shortcutsUsed[shortcut.ordinal()] = pressed;

        // Keep freezing if any shortcut is pressed
        for (boolean s : shortcutsUsed) {
            if (s) {
                activeTransform = false;
                inputShapes.setActiveTransform(false);
                return true;
            }
        }

        activeTransform = true;
        inputShapes.setActiveTransform(true);
        inputShapes.setMouseEngaged(false);
        return false;
    }

    // Enables or disables active shape
    private void updateActiveShape() {
        if (InputShapes.getToolsMode() == InputShapes.TOOLS.STAMP) {
            newActiveShape();
            enabled = true;
            return;
        }

        if (activeShape != null) sceneManager.getScene().removeObjectFromScene(activeShape);
        enabled = false;
    }

    private void stampShape() {
        // Update transform because might be actively changing
        Transform2D<Vector2f> transformShape = activeShape.getComponent(Transform2D.class);
        if (transformShape != null) {
            transform.setRotation(transformShape.getRotation());
            transform.setScale(transformShape.getScale());
        }

        Transform2D<Vector2f> copyTransform = transform.copy(); // Shouldn't be a reference

        activeShape.setTransform(copyTransform);
        sceneManager.getScene().removeObjectFromScene(activeShape);
        actionHandler.perform(new ActionStamp(sceneManager.getScene(), activeShape));

        newActiveShape();
    }

    // Save the previous active shape and create a new one
    private void newActiveShape() {
        Shape previousShape = activeShape;

        activeShape = new Shape(selectedShape, currentSculpt);
        activeShape.setTransform(transform);
        activeShape.setColour(colourSelected);
        activeShape.setShapeMode(stampMode);

        if (previousShape != null) {
            activeShape.setBlend(previousShape.getBlend());
            ComponentRounded rounded = previousShape.getComponent(ComponentRounded.class);
            if (rounded != null) {
                activeShape.addComponent(new ComponentRounded());
                activeShape.getComponent(ComponentRounded.class).setRounded(rounded.getRounded());
            }
        };

        sceneManager.getScene().addObjectToScene(activeShape);
    }
}
