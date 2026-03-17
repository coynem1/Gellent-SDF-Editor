package Input;

import Input.Actions.ActionHandler;
import Input.Actions.ActionStamp;
import Jade.Camera;
import Jade.Scene;
import Jade.SceneManager;
import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.Components.Blending;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GsonSaver;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Transform2D;
import util.WorldCoords;

import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class InputStampShapes {
    private static final float MOUSE_ENGAGE_DIST = 1f;

    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private Blending blending = new Blending();
    private SculptObject currentSculpt = new SculptObject();
    private Shape activeShape = null;

    private Scene currentScene;
    private SceneManager sceneManager;
    private ActionHandler actionHandler;
    private GsonSaver gsonSaver;
    private Camera camera;

    private Vector2f mousePos = new Vector2f();
    private boolean shortcutsUsed[] = new boolean[InputShapes.SHORTCUTS.values().length];
    private boolean mouseEngaged = false;   // Mouse has been moved enough to engage selected shortcuts
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

    public void init() {
        this.actionHandler = sceneManager.getActionHandler();
        this.gsonSaver = sceneManager.getGsonSaver();

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
            if (ImGui.getIO().getWantCaptureMouse()) { return; }
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

            if (toolsMode != InputShapes.TOOLS.STAMP) return;

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
            if (toolsMode != InputShapes.TOOLS.STAMP) return;

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

    // Rounds through mouse position
    private void roundShortcut(boolean pressed) {
        // Check if round-able
        if (activeShape.getComponent(ComponentRounded.class) == null) return;

        if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROUND)) return;

        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, camera);
        Vector2f prevWorldPos = transform.getPosition();
        ComponentRounded rounded = activeShape.getComponent(ComponentRounded.class);
        if (rounded == null) return;

        // Enabled only if the mouse has moved enough
        if (worldPos.distance(prevWorldPos) > MOUSE_ENGAGE_DIST) mouseEngaged = true;
        if (!mouseEngaged) {
            // Easy reset rounded shortcut
            rounded.setRounded(0f);
            return;
        }

        float round = worldPos.distance(prevWorldPos) / activeShape.getTransform().getScale() ;
        rounded.setRounded(min(round, ComponentRounded.MAX_ROUNDED));
    }

    // Rotates through mouse position
    private void rotateShortcut(boolean pressed) {
        if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROTATE)) return;

        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, camera);
        Vector2f prevWorldPos = transform.getPosition();
        float angle = (float) Math.atan2(worldPos.y - prevWorldPos.y, worldPos.x - prevWorldPos.x);
        transform.setRotation(angle + (float) Math.PI * 1.5f);  // Offset to point top of shape towards mouse
    }

    // Scales through mouse position
    private void scaleShortcut(boolean pressed) {
        if (freezeActiveShape(pressed, InputShapes.SHORTCUTS.SCALE)) return;

        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, camera);
        Vector2f prevWorldPos = transform.getPosition();
        transform.setScale(max(worldPos.distance(prevWorldPos), Shape.MINIMUM_SCALE));
    }

    // Stops active shape from changing position
    private boolean freezeActiveShape(boolean pressed, InputShapes.SHORTCUTS shortcut) {
        if (activeShape == null) return true;

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

    // Enables or disables active shape
    private void updateActiveShape() {
        if (toolsMode == InputShapes.TOOLS.STAMP) {
            newActiveShape();
            return;
        }

        if (activeShape != null) currentScene.removeObjectFromScene(activeShape);
    }

    // Change select/stamp mode
    private void toggleMode() {
        if (toolsMode == InputShapes.TOOLS.SELECT) return;

        // Intersect switches to difference too
        if (stampMode == InputShapes.MODES.DIFFERENCE) {
            stampMode = InputShapes.MODES.UNION;
        }
        else {
            stampMode = InputShapes.MODES.DIFFERENCE;
        }

        if (activeShape != null) activeShape.setShapeMode(stampMode);
    }

    private void stampShape() {
        Transform2D<Vector2f> copyTransform = transform.copy(); // Shouldn't be a reference

        activeShape.setTransform(copyTransform);
        currentScene.removeObjectFromScene(activeShape);
        actionHandler.perform(new ActionStamp(currentScene, activeShape));

        newActiveShape();
    }

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

    public static Vector3f getColourSelected() { return colourSelected; }
}
