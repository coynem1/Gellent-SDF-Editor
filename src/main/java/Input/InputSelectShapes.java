package Input;

import Jade.SceneManager;
import Observers.InputImGui;
import Observers.InputKeyEvents;
import Observers.InputMouseEvents;
import Rendering.Objects.GameObject;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.joml.Vector2f;
import util.CalculateSDF;
import util.Transform2D;
import util.WorldCoords;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class InputSelectShapes {
    private SculptObject selectedSculpt = new SculptObject();
    private Shape selectedShape = null;
    private GameObject selectedObject = null;

    private SceneManager sceneManager;
    private InputShapes inputShapes;

    private Vector2f mousePos = new Vector2f();
    private boolean shortcutsUsed[] = new boolean[InputShapes.SHORTCUTS.values().length];
    private boolean mouseEngaged = false;   // Mouse has been moved enough to engage selected shortcuts
    private boolean activeTransform = true; // Active shape tracks mouse position
    private InputShapes.TOOLS toolsMode = InputShapes.TOOLS.SELECT;
    private InputShapes.MODES stampMode = InputShapes.MODES.UNION;
    private boolean enabled = false;

    public InputSelectShapes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void init(InputShapes inputShapes) {
        this.inputShapes = inputShapes;

        selectedObject = new Shape(InputShapes.SHAPES.BOX, selectedSculpt);

        updateActiveShape();
        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onColourChanged((colour) -> {
            if (!enabled) return;
            if (selectedShape != null) selectedShape.setColour(colour);
        });
        InputImGui.onScaleChanged((scale) -> {
            if (!enabled) return;
            if (selectedShape != null) selectedShape.getTransform().setScale(scale);
        });
        InputImGui.onBlendChanged((blend) -> {
            if (!enabled) return;
            if (selectedShape != null) selectedShape.setBlend(blend);
        });
        InputImGui.onRotationChanged((rotation) -> {
            if (!enabled) return;
            if (selectedShape != null) {
                // Convert to radians
                rotation *= (float) Math.PI / 180;
                selectedShape.getTransform().setRotation(rotation);
            }
        });
        InputImGui.onToolChanged((tool) -> {
            toolsMode = tool;
            updateActiveShape();
        });

        InputMouseEvents.onBtnPressed((button, _) -> {
            // If focused on UI or not enabled, ignore
            if (ImGui.getIO().getWantCaptureMouse() || !enabled) { return; }

            // Grab hovered shape
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    selectShape();
                    break;
            }
        });
        InputMouseEvents.onBtnReleased((button, _) -> {
            // Grab hovered shape
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    activeTransform = false;
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            // If focused on UI or not enabled, ignore
            if (ImGui.getIO().getWantCaptureMouse() || !enabled) { return; }

            mousePos = new Vector2f(xPos, yPos);

            // Active shape changing
            if (!activeTransform) {
                shapeShortcut();
            }
        });

        // Shortcuts
        InputKeyEvents.onKeyPressed((key, _, mods) -> {
            boolean ctrl  = (mods & GLFW_MOD_CONTROL) != 0;
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;

            if (!enabled) return;

            if (!ctrl && !shift) {
                switch (key) {
                    case GLFW_KEY_BACKSPACE:
                        if (selectedObject.getClass() == Shape.class) inputShapes.toggleMode((Shape) selectedObject);
                        break;
                    case GLFW_KEY_S:
                        inputShapes.scaleShortcut(selectedObject);
                        break;
                    case GLFW_KEY_R:
                        inputShapes.rotateShortcut(selectedObject);
                        break;
                    case GLFW_KEY_F:
                        inputShapes.roundShortcut(selectedObject);
                        break;
                }
            }
        });

        // Let go of key
        InputKeyEvents.onKeyReleased((key, _, _) -> {
            if (!enabled) return;
            switch (key) {
                case GLFW_KEY_S:
                    inputShapes.scaleShortcut(selectedObject);
                    break;
                case GLFW_KEY_R:
                    inputShapes.rotateShortcut(selectedObject);
                    break;
                case GLFW_KEY_F:
                    inputShapes.roundShortcut(selectedObject);
                    break;
            }
        });
    }

    private void shapeShortcut() {
        if (selectedObject == null) return;

        if (shortcutsUsed[InputShapes.SHORTCUTS.ROTATE.ordinal()]) { inputShapes.rotateShortcut(selectedObject); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.SCALE.ordinal()]) { inputShapes.scaleShortcut(selectedObject); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.ROUND.ordinal()]) { inputShapes.roundShortcut(selectedObject); }
    }

    // TODO: Selects shape you hover on before interacting with it
    private void selectShape() {
        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());

        // Find hovered shape
        for (GameObject object : sceneManager.getScene().getObjects()) {
            if (object.getClass() != Shape.class) continue;
            if (CalculateSDF.calculateSDF((Shape) object, worldPos) <= 0) {
                selectedObject = object;
                return;
            }
        }

    }

    // Stops active shape from changing position
    private boolean freezeActiveShape(boolean pressed, InputShapes.SHORTCUTS shortcut) {
        if (selectedShape == null) return true;

        // Shortcut array
        shortcutsUsed[shortcut.ordinal()] = pressed;

        // Keep freezing if any shortcut is pressed
        for (boolean s : shortcutsUsed) {
            if (s) {
                activeTransform = false;
                return false;
            }
        }

        activeTransform = true;
        mouseEngaged = false;
        return true;
    }

    // TODO: Changes active shape to selected shape
    private void updateActiveShape() {
        if (toolsMode != InputShapes.TOOLS.SELECT) {
            enabled = false;
            return;
        }
        enabled = true;
    }
}
