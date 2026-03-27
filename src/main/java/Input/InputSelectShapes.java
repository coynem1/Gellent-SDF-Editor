package Input;

import Input.Actions.ActionHandler;
import Jade.Camera;
import Jade.Scene;
import Jade.SceneManager;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.joml.Vector2f;
import util.WorldCoords;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class InputSelectShapes {
    private SculptObject selectedSculpt = new SculptObject();
    private Shape selectedShape = null;

    private SceneManager sceneManager;
    private InputShapes inputShapes;

    private Vector2f mousePos = new Vector2f();
    private boolean shortcutsUsed[] = new boolean[InputShapes.SHORTCUTS.values().length];
    private boolean mouseEngaged = false;   // Mouse has been moved enough to engage selected shortcuts
    private boolean activeTransform = true; // Active shape tracks mouse position
    private InputShapes.TOOLS toolsMode = InputShapes.TOOLS.SELECT;
    private InputShapes.MODES stampMode = InputShapes.MODES.UNION;

    public InputSelectShapes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void init(InputShapes inputShapes) {
        this.inputShapes = inputShapes;

        updateActiveShape();
        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onColourChanged((colour) -> {
            if (selectedShape != null) selectedShape.setColour(colour);
        });
        InputImGui.onScaleChanged((scale) -> {
            if (selectedShape != null) selectedShape.getTransform().setScale(scale);
        });
        InputImGui.onBlendChanged((blend) -> {
            if (selectedShape != null) selectedShape.setBlend(blend);
        });
        InputImGui.onRotationChanged((rotation) -> {
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
            // If focused on UI, ignore
            if (ImGui.getIO().getWantCaptureMouse()) { return; }

            switch (button) {
                // Change Scene
                case GLFW_MOUSE_BUTTON_LEFT:
                    if (toolsMode == InputShapes.TOOLS.SELECT) { selectShape(); }
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            // If focused on UI, ignore
            if (ImGui.getIO().getWantCaptureMouse()) { return; }

            mousePos = new Vector2f(xPos, yPos);
            Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());

            // transform.setPosition(worldPos);
        });

        // Shortcuts
        InputKeyEvents.onKeyPressed((key, _, mods) -> {
            boolean ctrl  = (mods & GLFW_MOD_CONTROL) != 0;
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;

            if (!ctrl && !shift) {
                switch (key) {
                    case GLFW_KEY_BACKSPACE:
                        toggleMode();
                        break;
                    case GLFW_KEY_S:
                        scaleShortcut(true);
                        break;
                    case GLFW_KEY_R:
                        rotateShortcut(true);
                        break;
                    case GLFW_KEY_F:
                        roundShortcut(true);
                        break;
                }
            }
        });

        // Let go of key
        InputKeyEvents.onKeyReleased((key, _, _) -> {
            switch (key) {
                case GLFW_KEY_S:
                    scaleShortcut(false);
                    break;
                case GLFW_KEY_R:
                    rotateShortcut(false);
                    break;
                case GLFW_KEY_F:
                    roundShortcut(false);
                    break;
            }
        });
    }

    private void shapeShortcut() {
        if (shortcutsUsed[InputShapes.SHORTCUTS.ROTATE.ordinal()]) { rotateShortcut(true); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.SCALE.ordinal()]) { scaleShortcut(true); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.ROUND.ordinal()]) { roundShortcut(true); }
    }

    // TODO: Selects shape you click on
    private void selectShape() {}

    // Rounds through mouse position
    private void roundShortcut(boolean pressed) {
        return;
    }

    // Rotates through mouse position
    private void rotateShortcut(boolean pressed) {
    }

    // Scales through mouse position
    private void scaleShortcut(boolean pressed) {
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

    }

    // Change select union/difference mode
    private void toggleMode() {
        if (toolsMode != InputShapes.TOOLS.SELECT) return;

        if (selectedShape != null) selectedShape.setShapeMode(stampMode);
    }
}
