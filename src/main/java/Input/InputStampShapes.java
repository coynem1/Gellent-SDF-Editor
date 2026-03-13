package Input;

import Input.Actions.ActionHandler;
import Input.Actions.ActionStamp;
import Jade.Camera;
import Jade.Scene;
import Jade.SceneManager;
import Jade.Window;
import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.Components.Blending;
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
    public enum TOOLS { SELECT, STAMP, SMEAR};
    public enum SHAPES { CIRCLE, BOX, TRIANGLE, HEXAGON};
    public static final HashMap<TOOLS, String> TOOL_NAMES = new HashMap<>() {{
        put(TOOLS.SELECT, "Select");
        put(TOOLS.STAMP, "Stamp");
        put(TOOLS.SMEAR, "Smear");
    }};
    public static final HashMap<SHAPES, String> SHAPE_NAMES = new HashMap<>() {{
        put(SHAPES.CIRCLE, "Circle");
        put(SHAPES.BOX, "Box");
        put(SHAPES.TRIANGLE, "Triangle");
        put(SHAPES.HEXAGON, "Hexagon");
    }};

    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private Blending blending = new Blending();
    private SculptObject currentSculpt = new SculptObject();
    private Scene currentScene;
    private SceneManager sceneManager;
    private ActionHandler actionHandler;
    private Vector2f mousePos = new Vector2f();
    private SHAPES selectedShape = SHAPES.CIRCLE;

    private TOOLS toolsMode = TOOLS.SELECT;
    private Camera camera;


    public InputStampShapes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.currentScene = sceneManager.getScene();
        this.camera = this.currentScene.getCamera();
    }

    public void init() {
        this.actionHandler = sceneManager.getActionHandler();
        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onColourChanged((colour) -> {
            colourSelected = colour;
        });
        InputImGui.onScaleChanged((scale) -> {
            transform.setScale(scale);
        });
        InputImGui.onBlendChanged((blend) -> {
            blending.setBlend(blend);
        });
        InputImGui.onRotationChanged((rotation) -> {
            // Convert to radians
            rotation *= (float) Math.PI / 180;
            transform.setRotation(rotation);
        });
        InputImGui.onShapeChanged((shape) -> {
            selectedShape = shape;
        });

        InputMouseEvents.onBtnPressed((button, _) -> {
            // If clicking on UI, ignore
            if (ImGui.getIO().getWantCaptureMouse()) { return; }

            switch (button) {
                // Change Scene
                case GLFW_MOUSE_BUTTON_LEFT:
                    if (toolsMode == TOOLS.STAMP) { stampShape(); }
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            mousePos = new Vector2f(xPos, yPos);
            Vector2f worldPos = WorldCoords.screenToWorld(mousePos, camera);
            transform.setPosition(worldPos);
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
                }
            }
        });
    }

    private void stampShape() {
        Transform2D<Vector2f> copyTransform = transform.copy(); // Shouldn't be a reference
        Shape shape = new Shape(selectedShape, currentSculpt);

        shape.setTransform(copyTransform);
        shape.setBlend(blending.getBlend());
        shape.setColour(colourSelected);

        actionHandler.perform(new ActionStamp(currentScene, shape));
        // currentScene.addObjectToScene(shape);
    }

    public static Vector3f getColourSelected() { return colourSelected; }

    public void setToolsMode(TOOLS mode) { this.toolsMode = mode; }
}
