package Input;

import Jade.SceneManager;
import Observers.InputImGui;
import Observers.InputKeyEvents;
import Observers.InputMouseEvents;
import Observers.InputShapesEvents;
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
    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private SculptObject selectedSculpt = new SculptObject();
    private Shape selectedShape = null;
    private GameObject selectedObject = null;

    private SceneManager sceneManager;
    private InputShapes inputShapes;

    private Vector2f mousePos = new Vector2f();
    private boolean shortcutsUsed[] = new boolean[InputShapes.SHORTCUTS.values().length];
    private boolean usingShortcuts = true; // Active shape tracks mouse position
    private boolean moving = false;
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
        // ImGui UI inputs
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
            updateActiveShape();
        });
        InputShapesEvents.onToolModeChanged((tool) -> {
            updateActiveShape();
        });

        InputMouseEvents.onBtnPressed((button, _) -> {
            // If focused on UI or not enabled, ignore
            if (ImGui.getIO().getWantCaptureMouse() || !enabled) { return; }
            Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
            Transform2D<Vector2f> transformComponent;

            // Grab hovered shape
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    selectShape();
                    if (selectedObject == null) return;
                    moving = true;

                    transformComponent = selectedObject.getComponent(Transform2D.class);
                    if (transformComponent == null) return;

                    transform.setPosition(worldPos.sub(transformComponent.getPosition()));
                    moveSelected();
                    break;
            }
        });
        InputMouseEvents.onBtnReleased((button, _) -> {
            // Grab hovered shape
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    moving = false;
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            // If focused on UI or not enabled, ignore
            if (ImGui.getIO().getWantCaptureMouse() || !enabled) { return; }

            mousePos = new Vector2f(xPos, yPos);

            // Active shape changing
            if (!usingShortcuts) shapeShortcut();

            // Move the selected object
            if (moving) moveSelected();
        });

        // Shortcuts
        InputKeyEvents.onKeyPressed((key, _, mods) -> {
            boolean ctrl  = (mods & GLFW_MOD_CONTROL) != 0;
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
            boolean pressed = true;

            if (!enabled) return;

            if (!ctrl && !shift) {
                switch (key) {
                    case GLFW_KEY_BACKSPACE:
                        if (selectedObject.getClass() == Shape.class) {
                            Shape shape = (Shape) selectedObject;
                            shape.setShapeMode(inputShapes.toggleMode(shape));
                            selectedObject = shape;
                        }
                        break;
                    case GLFW_KEY_S:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.SCALE);
                        if (selectedObject != null) inputShapes.scaleShortcut(selectedObject);
                        break;
                    case GLFW_KEY_R:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROTATE);
                        if (selectedObject != null) inputShapes.rotateShortcut(selectedObject);
                        break;
                    case GLFW_KEY_B:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.BLEND);
                        if (selectedObject != null) inputShapes.blendShortcut(selectedObject);
                        break;
                    case GLFW_KEY_F:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROUND);
                        if (selectedObject != null) inputShapes.roundShortcut(selectedObject);
                        break;
                }
            }
        });

        // Let go of key
        InputKeyEvents.onKeyReleased((key, _, _) -> {
            boolean pressed = false;
            if (!enabled) return;

            Transform2D<Vector2f> transformShape = null;
            if (selectedObject != null) transformShape = selectedObject.getComponent(Transform2D.class);

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

    // Select the hovered shape and move it to the mouse position
    private void moveSelected() {
        if (selectedObject == null || !enabled) return;

        selectedObject = inputShapes.moveObject(selectedObject, transform.getPosition());
    }

    private void shapeShortcut() {
        if (selectedObject == null || moving) return;

        if (shortcutsUsed[InputShapes.SHORTCUTS.ROTATE.ordinal()]) { selectedObject = inputShapes.rotateShortcut(selectedObject); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.SCALE.ordinal()]) { selectedObject = inputShapes.scaleShortcut(selectedObject); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.BLEND.ordinal()]) { selectedObject = inputShapes.blendShortcut(selectedObject); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.ROUND.ordinal()]) { selectedObject = inputShapes.roundShortcut(selectedObject); }
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
        selectedObject = null;
    }

    // Stops active shape from changing position
    private boolean freezeActiveShape(boolean pressed, InputShapes.SHORTCUTS shortcut) {
        if (selectedObject == null) return true;

        // Shortcut array
        shortcutsUsed[shortcut.ordinal()] = pressed;

        // Keep freezing if any shortcut is pressed
        for (boolean s : shortcutsUsed) {
            if (s) {
                usingShortcuts = false;
                inputShapes.setActiveTransform(false);
                return false;
            }
        }

        usingShortcuts = true;
        inputShapes.setMouseEngaged(false);
        inputShapes.setActiveTransform(true);
        return true;
    }

    // TODO: Changes active shape to selected shape
    private void updateActiveShape() {
        if (InputShapes.getToolsMode() != InputShapes.TOOLS.SELECT) {
            enabled = false;
            // selectedObject = null;
            return;
        }
        enabled = true;
    }
}
