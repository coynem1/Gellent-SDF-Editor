package Input;

import Input.Actions.ActionHandler;
import Input.Actions.ActionStamp;
import Jade.Camera;
import Jade.Scene;
import Jade.SceneManager;
import Jade.Window;
import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.Components.Blending;
import Rendering.Objects.GsonSaver;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import util.Transform2D;
import util.WorldCoords;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class InputStampShapes {
    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    public enum TOOLS { SELECT, STAMP, SMEAR };
    public enum SHAPES { CIRCLE, BOX, TRIANGLE, STAR};
    public enum MODES { UNION, DIFFERENCE, INTERSECTION };
    public static final HashMap<TOOLS, String> TOOL_NAMES = new HashMap<>() {{
        put(TOOLS.SELECT, "Select");
        put(TOOLS.STAMP, "Stamp");
        put(TOOLS.SMEAR, "Smear");
    }};
    public static final HashMap<SHAPES, String> SHAPE_NAMES = new HashMap<>() {{
        put(SHAPES.CIRCLE, "Circle");
        put(SHAPES.BOX, "Box");
        put(SHAPES.TRIANGLE, "Triangle");
        put(SHAPES.STAR, "Star");
    }};

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
    private SHAPES selectedShape = SHAPES.CIRCLE;
    private TOOLS toolsMode = TOOLS.SELECT;
    private MODES stampMode = MODES.UNION;


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
            if (activeShape != null) activeShape.setColour(colour);
        });
        InputImGui.onScaleChanged((scale) -> {
            transform.setScale(scale);
            if (activeShape != null) activeShape.setTransform(transform);
        });
        InputImGui.onBlendChanged((blend) -> {
            blending.setBlend(blend);
            if (activeShape != null) activeShape.setBlend(blend);
        });
        InputImGui.onRotationChanged((rotation) -> {
            // Convert to radians
            rotation *= (float) Math.PI / 180;
            transform.setRotation(rotation);
            if (activeShape != null) activeShape.setTransform(transform);
        });
        InputImGui.onShapeChanged((shape) -> {
            selectedShape = shape;
            if (activeShape != null) activeShape.setShape(shape);
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
                    if (toolsMode == TOOLS.STAMP) { stampShape(); }
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            // If focused on UI, ignore
            if (ImGui.getIO().getWantCaptureMouse()) { return; }

            mousePos = new Vector2f(xPos, yPos);
            Vector2f worldPos = WorldCoords.screenToWorld(mousePos, camera);
            transform.setPosition(worldPos);
            if (activeShape != null) activeShape.setTransform(transform);
        });

        // Shortcuts
        InputKeyEvents.onKeyPressed((key, _, mods) -> {
            boolean ctrl  = (mods & GLFW_MOD_CONTROL) != 0;
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;

            if (ctrl && shift) {
                switch (key) {
                    case GLFW_KEY_Z:
                        actionHandler.redo();
                        break;
                }
            }
            else if (ctrl) {
                switch (key) {
                    case GLFW_KEY_Z:
                        actionHandler.undo();
                        break;
                    case GLFW_KEY_S:
                        gsonSaver.save();
                        break;
                }
            }
            switch (key) {
                case GLFW_KEY_BACKSPACE:
                    toggleMode();
                    break;
                case GLFW_KEY_S:
                    // Scale shortcut
                    // gsonSaver.save();
                    break;
            }
        });
    }

    // Enables or disables active shape
    private void updateActiveShape() {
        if (toolsMode == TOOLS.STAMP) {
            newActiveShape();
            return;
        }

        if (activeShape != null) currentScene.removeObjectFromScene(activeShape);
    }

    // Change select/stamp mode
    private void toggleMode() {
        if (toolsMode == TOOLS.SELECT) return;

        // Intersect switches to difference too
        if (stampMode == MODES.DIFFERENCE) {
            stampMode = MODES.UNION;
        }
        else {
            stampMode = MODES.DIFFERENCE;
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
        activeShape = new Shape(selectedShape, currentSculpt);
        activeShape.setTransform(transform);
        activeShape.setBlend(blending.getBlend());
        activeShape.setColour(colourSelected);
        activeShape.setShapeMode(stampMode);
        currentScene.addObjectToScene(activeShape);
    }

    public static Vector3f getColourSelected() { return colourSelected; }
}
