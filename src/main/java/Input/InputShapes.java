package Input;

import Input.Actions.ActionHandler;
import Jade.Camera;
import Jade.Scene;
import Jade.SceneManager;
import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GameObject;
import Saving.GsonSaver;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Transform2D;
import util.WorldCoords;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class InputShapes {
    private static final float MOUSE_ENGAGE_DIST = 0.1f;

    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    public enum TOOLS { SELECT, STAMP, SMEAR };
    public enum SHAPES { CIRCLE, BOX, TRIANGLE, STAR};
    public enum MODES { UNION, DIFFERENCE, INTERSECTION };
    public enum SHORTCUTS { SCALE, ROTATE, ROUND };
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
    public static final SHAPES[] UNROUNDABLE_SHAPES = { SHAPES.CIRCLE };

    private InputStampShapes inputStamper;
    private InputSelectShapes inputSelector;
    private Scene currentScene;
    private SceneManager sceneManager;
    private ActionHandler actionHandler;
    private GsonSaver gsonSaver;
    private Camera camera;

    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private Vector2f mousePos = new Vector2f();
    private boolean mouseEngaged = false;   // Mouse has been moved enough to engage selected shortcuts
    private boolean activeTransform = true; // Active shape tracks mouse position
    private InputShapes.TOOLS toolsMode = InputShapes.TOOLS.SELECT;

    public InputShapes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        this.inputStamper = new InputStampShapes(sceneManager);
        this.inputSelector = new InputSelectShapes(sceneManager);
        transform.setPosition(new Vector2f(0f, 0f));

        bindInputs();
    }

    public void init() {
        this.actionHandler = sceneManager.getActionHandler();
        this.gsonSaver = sceneManager.getGsonSaver();
        this.inputStamper.init(this);
        this.inputSelector.init(this);

        // bindInputs();
    }

    private void bindInputs() {
        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            // If focused on UI, ignore
            if (ImGui.getIO().getWantCaptureMouse()) { return; }

            mousePos = new Vector2f(xPos, yPos);
            Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());

            // Active shape changing
            if (!activeTransform) return;

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
                    case GLFW_KEY_S:
                        gsonSaver.save(true);
                        break;
                    case GLFW_KEY_O:
                        gsonSaver.load();
                        break;
                }
            }
        });
    }

    // Change select/stamp mode
    public MODES toggleMode(@NotNull Shape shape) {
        MODES shapeMode = MODES.values()[shape.getShapeMode()];

        // Intersect switches to difference too
        if (shapeMode == MODES.DIFFERENCE) {
            shapeMode = MODES.UNION;
        }
        else {
            shapeMode = MODES.DIFFERENCE;
        }

        shape.setShapeMode(shapeMode);
        return shapeMode;
    }

    // Rounds through mouse position
    public GameObject roundShortcut(@NotNull GameObject object, boolean pressed) {
        ComponentRounded rounded = object.getComponent(ComponentRounded.class);

        // If finished rounding, and it's zero, remove the rounded component
        if (!pressed) {
            if (rounded == null) return object;
            if (rounded.getRounded() == 0f) {
                object.removeComponents(ComponentRounded.class);
                return object;
            }
        }

        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        Vector2f prevWorldPos = transform.getPosition();


        // If roundable shape, add the round component
        if (rounded == null) {
            if (object.getClass() != Shape.class) return object;
            Shape shape = (Shape) object;

            // Check if unroundable
            for (var unroundable : UNROUNDABLE_SHAPES) {
                if (shape.getShapeType() == unroundable) return object;
            }

            // Add round component
            shape.addComponent(new ComponentRounded());
            rounded = object.getComponent(ComponentRounded.class);
        }

        // Enabled only if the mouse has moved enough
        if (worldPos.distance(prevWorldPos) > MOUSE_ENGAGE_DIST) mouseEngaged = true;
        if (!mouseEngaged) {
            // Easy reset rounded shortcut
            rounded.setRounded(0f);
            return object;
        }

        if (object.getClass() != Shape.class) return object;
        float round = worldPos.distance(prevWorldPos) / ((Shape) object).getTransform().getScale() ;
        rounded.setRounded(min(round, ComponentRounded.MAX_ROUNDED));
        return object;
    }

    // Rotates through mouse position
    public GameObject rotateShortcut(@NotNull GameObject object) {
        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        Vector2f prevWorldPos = transform.getPosition();

        float angle = (float) Math.atan2(worldPos.y - prevWorldPos.y, worldPos.x - prevWorldPos.x);
        angle = angle + (float) Math.PI * 1.5f; // Offset to point top of object towards mouse
        transform.setRotation(angle);

        if (object.getClass() == Shape.class) ((Shape) object).getTransform().setRotation(angle);
        return object;
    }

    // Scales through mouse position
    public GameObject scaleShortcut(@NotNull GameObject object) {
        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        Vector2f prevWorldPos = transform.getPosition();
        float scale = max(worldPos.distance(prevWorldPos), Shape.MINIMUM_SCALE);
        transform.setScale(scale);

        ((Shape) object).getTransform().setScale(scale);
        return object;
    }

    public void setMouseEngaged(boolean engaged) {this.mouseEngaged = engaged;}
    public void setActiveTransform(boolean active) {this.activeTransform = active;}

    public static Vector3f getColourSelected() { return colourSelected; }
}
