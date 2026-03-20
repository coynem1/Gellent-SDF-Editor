package Input;

import Input.Actions.ActionHandler;
import Input.Actions.ActionStamp;
import Jade.Camera;
import Jade.Scene;
import Jade.SceneManager;
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

    private Scene currentScene;
    private SceneManager sceneManager;
    private ActionHandler actionHandler;
    private InputShapes inputShapes;
    private Camera camera;

    private Vector2f mousePos = new Vector2f();
    private boolean shortcutsUsed[] = new boolean[InputShapes.SHORTCUTS.values().length];
    private boolean activeTransform = true; // Active shape tracks mouse position
    private InputShapes.SHAPES selectedShape = InputShapes.SHAPES.CIRCLE;
    private InputShapes.TOOLS toolsMode = InputShapes.TOOLS.SELECT;
    private InputShapes.MODES stampMode = InputShapes.MODES.UNION;


    public InputStampShapes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.currentScene = sceneManager.getScene();
        this.camera = this.currentScene.getCamera();

        transform.setPosition(new Vector2f(0f, 0f));
    }

    public void init(InputShapes inputShapes) {
        this.actionHandler = sceneManager.getActionHandler();
        this.inputShapes = inputShapes;

        updateActiveShape();
        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onColourChanged((colour) -> {
            colourSelected = colour;
            if (toolsMode != InputShapes.TOOLS.STAMP) return;
            if (activeShape != null) activeShape.setColour(colour);
        });
        InputImGui.onScaleChanged((scale) -> {
            transform.setScale(scale);
            if (toolsMode != InputShapes.TOOLS.STAMP) return;
            if (activeShape != null) activeShape.setTransform(transform);
        });
        InputImGui.onBlendChanged((blend) -> {
            blending.setBlend(blend);
            if (toolsMode != InputShapes.TOOLS.STAMP) return;
            if (activeShape != null) activeShape.setBlend(blend);
        });
        InputImGui.onRotationChanged((rotation) -> {
            // Convert to radians
            rotation *= (float) Math.PI / 180;
            transform.setRotation(-rotation);
            if (toolsMode != InputShapes.TOOLS.STAMP) return;
            if (activeShape != null) activeShape.setTransform(transform);
        });
        InputImGui.onShapeChanged((shape) -> {
            selectedShape = shape;
            if (toolsMode != InputShapes.TOOLS.STAMP) return;
            if (activeShape != null) activeShape.setShape(shape);
        });
        InputImGui.onToolChanged((tool) -> {
            toolsMode = tool;
            updateActiveShape();
        });

        InputMouseEvents.onBtnPressed((button, _) -> {
            // If focused on UI, ignore
            if (ImGui.getIO().getWantCaptureMouse()) { return; }
            if (toolsMode != InputShapes.TOOLS.STAMP) return;

            switch (button) {
                // Change Scene
                case GLFW_MOUSE_BUTTON_LEFT:
                    stampShape();
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            // If focused on UI, ignore
            if (ImGui.getIO().getWantCaptureMouse()) return;
            if (toolsMode != InputShapes.TOOLS.STAMP) return;

            mousePos = new Vector2f(xPos, yPos);
            Vector2f worldPos = WorldCoords.screenToWorld(mousePos, camera);

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

            if (toolsMode != InputShapes.TOOLS.STAMP) return;

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
                    case GLFW_KEY_F:
                        if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROUND)) {
                            inputShapes.setMouseEngaged(false);
                            object = inputShapes.roundShortcut(object);
                        }
                        break;
                }
            }

            activeShape = (Shape) object;
        });

        // Let go of key
        InputKeyEvents.onKeyReleased((key, _, _) -> {
            boolean pressed = false;
            GameObject object = activeShape;
            if (toolsMode != InputShapes.TOOLS.STAMP) return;

            switch (key) {
                case GLFW_KEY_S:
                    if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.SCALE)) object = inputShapes.scaleShortcut(object);
                    break;
                case GLFW_KEY_R:
                    if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROTATE)) object = inputShapes.rotateShortcut(object);
                    break;
                case GLFW_KEY_F:
                    if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROUND)) object = inputShapes.roundShortcut(object);
                    break;
            }

            activeShape = (Shape) object;
        });
    }

    // Shortcuts for active shape
    private void shapeShortcut() {
        GameObject object = activeShape;

        if (shortcutsUsed[InputShapes.SHORTCUTS.ROTATE.ordinal()]) { object = inputShapes.rotateShortcut(object); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.SCALE.ordinal()]) { object = inputShapes.scaleShortcut(object); }
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
        return false;
    }

    // Enables or disables active shape
    private void updateActiveShape() {
        if (toolsMode == InputShapes.TOOLS.STAMP) {
            newActiveShape();
            return;
        }

        if (activeShape != null) currentScene.removeObjectFromScene(activeShape);
    }

    private void stampShape() {
        Transform2D<Vector2f> copyTransform = transform.copy(); // Shouldn't be a reference

        activeShape.setTransform(copyTransform);
        currentScene.removeObjectFromScene(activeShape);
        actionHandler.perform(new ActionStamp(currentScene, activeShape));

        newActiveShape();
    }

    // Save the previous active shape and create a new one
    private void newActiveShape() {
        Shape previousShape = activeShape;

        activeShape = new Shape(selectedShape, currentSculpt);
        activeShape.setTransform(transform);
        activeShape.setBlend(blending.getBlend());
        activeShape.setColour(colourSelected);
        activeShape.setShapeMode(stampMode);

        if (previousShape != null) {
            ComponentRounded rounded = previousShape.getComponent(ComponentRounded.class);
            if (rounded != null) {
                activeShape.getComponent(ComponentRounded.class).setRounded(rounded.getRounded());
            }
        };

        currentScene.addObjectToScene(activeShape);
    }
}
