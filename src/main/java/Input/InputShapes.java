package Input;

import Input.Actions.ActionDelete;
import Input.Actions.ActionHandler;
import Input.Actions.ActionStamp;
import Jade.SceneManager;
import Observers.InputImGui;
import Observers.InputKeyEvents;
import Observers.InputMouseEvents;
import Observers.InputShapesEvents;
import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GameObject;
import Saving.GsonSaver;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import Rendering.Objects.Components.Transform2D;
import util.WorldCoords;

import java.util.EnumSet;
import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class InputShapes {
    private static final float MOUSE_ENGAGE_DIST = 1f;

    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    public enum TOOLS { SELECT, STAMP, SMEAR };
    public enum SHAPES { CIRCLE, BOX, TRIANGLE, STAR};
    public enum MODES { UNION, DIFFERENCE, INTERSECTION };
    public enum SHORTCUTS { MOVE, SCALE, ROTATE, ROUND, BLEND, MODE };
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
    public static final EnumSet<SHAPES> UNROUNDABLE_SHAPES = EnumSet.of(
            InputShapes.SHAPES.CIRCLE
    );

    private InputStampShapes inputStamper;
    private InputSelectShapes inputSelector;
    private SceneManager sceneManager;
    private ActionHandler actionHandler;
    private GsonSaver gsonSaver;

    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private Vector2f mousePos = new Vector2f();
    private boolean mouseEngaged = false;   // Mouse has been moved enough to engage selected shortcuts
    private boolean activeTransform = true; // Active shape tracks mouse position
    private static InputShapes.TOOLS toolsMode = InputShapes.TOOLS.SELECT;
    private GameObject currentObject = null;
    private GameObject copiedObject = null;

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
    }

    private void bindInputs() {
        InputImGui.onToolChanged((tool) -> {
            toolsMode = tool;
        });

        InputSaving.onOpened((_) ->{ reset(); });
        InputSaving.onNewFile(this::reset);

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
                    case GLFW_KEY_N:
                        sceneManager.newScene();
                        break;
                    case GLFW_KEY_V:
                        pasteObject();
                        break;
                }
            }
            else {
                switch (key) {
                    case GLFW_KEY_TAB:
                        toggleToolsMode();
                        break;
                }
            }

        });
    }

    // Set everything back to default, for new scenes
    public void reset() {
        mouseEngaged = false;
        activeTransform = true;
        currentObject = null;
        toolsMode = InputShapes.TOOLS.SELECT;
        transform.setPosition(new Vector2f());
        currentObject = null;

        inputSelector.reset();
        inputStamper.reset();
    }

    // Toggles between select/stamp
    private void toggleToolsMode() {
        // In case of smear, switch to select
        if (toolsMode == TOOLS.SELECT) {
            setToolsMode(TOOLS.STAMP);
        }
        else {
            setToolsMode(TOOLS.SELECT);
        }
    }

    // Change shape type
    public Shape changeShape(@NotNull Shape shape, @NotNull InputShapes.SHAPES newShapeType) {
        shape.setShape(newShapeType);
        return shape;
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
    public GameObject blendShortcut(@NotNull GameObject object) { return blendShortcut(object, -1f); }
    public GameObject blendShortcut(@NotNull GameObject object, float blend) {
        if (object.getClass() != Shape.class) return object;    // Only shapes can be blended
        Shape shape = (Shape) object;

        // Manually setting roundness?
        if (blend != -1f) {
            shape.setBlend(blend);
            return object;
        }

        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        Vector2f prevWorldPos = transform.getPosition();

        // Enabled only if the mouse has moved enough
        if (worldPos.distance(prevWorldPos) > MOUSE_ENGAGE_DIST) mouseEngaged = true;
        if (!mouseEngaged) {
            // Easy reset blend shortcut
            shape.setBlend(0f);
            InputShapesEvents.setBlendCallback(0f);
            return shape;
        }

        // Normal blending
        float blending = worldPos.distance(prevWorldPos) ;
        shape.setBlend(blending);
        return shape;
    }

    // Rounds through mouse position or manually
    public GameObject roundShortcut(@NotNull GameObject object) { return roundShortcut(object, -1f);}
    public GameObject roundShortcut(@NotNull GameObject object, float round) {
        float ROUNDED_SCALE = 10f;
        ComponentRounded rounded = object.getComponent(ComponentRounded.class);
        Transform2D<Vector2f> transformObj = object.getComponent(Transform2D.class);

        // Must be shape
        if (object.getClass() != Shape.class) return object;

        // Check if the shapes unroundable
        Shape shape = (Shape) object;
        if (InputShapes.UNROUNDABLE_SHAPES.contains(shape.getShapeType())) return object;

        // Add a rounded component if needed
        if (rounded == null) {
            rounded = new ComponentRounded();
            object.addComponent(rounded);
        }
        if (transformObj == null) return object;

        // Manually setting roundness?
        if (round != -1f) {
            rounded.setRounded(round / (transformObj.getScale() * ROUNDED_SCALE));
            return object;
        }

        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        Vector2f prevWorldPos = transform.getPosition();

        // Enabled only if the mouse has moved enough
        if (worldPos.distance(prevWorldPos) > MOUSE_ENGAGE_DIST) mouseEngaged = true;
        if (!mouseEngaged) {
            // Easy reset rounded shortcut
            rounded.setRounded(0f);
            return object;
        }

        // Normal Rounding
        float roundness = worldPos.distance(prevWorldPos) / (transformObj.getScale() * ROUNDED_SCALE) ;
        rounded.setRounded(roundness);
        return object;
    }

    // Rotates through mouse position
    public GameObject rotateShortcut(@NotNull GameObject object) {
        Transform2D<Vector2f> transformObj = object.getComponent(Transform2D.class);
        if (transformObj == null) return object;

        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        Vector2f prevWorldPos = transformObj.getPosition();
        Vector2f mousePrevPos = transform.getPosition();

        float angle = (float) Math.atan2(worldPos.y - prevWorldPos.y, worldPos.x - prevWorldPos.x);
        angle = angle + (float) Math.PI * 1.5f; // Offset to point top of object towards mouse
        transform.setRotation(angle);

        // Enabled only if the mouse has moved enough
        if (worldPos.distance(mousePrevPos) > MOUSE_ENGAGE_DIST) mouseEngaged = true;
        if (!mouseEngaged) {
            // Easy reset Rotation shortcut
            if (transformObj != null) transformObj.setRotation(0);
            return object;
        }

        if (transformObj != null) {
            transformObj.setRotation(angle);
        }
        return object;
    }

    // Scales through mouse position
    public GameObject scaleShortcut(@NotNull GameObject object) {
        Transform2D<Vector2f> transformObj = object.getComponent(Transform2D.class);
        if (transformObj == null) return object;

        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        Vector2f prevWorldPos = transformObj.getPosition();

        float scale = max(worldPos.distance(prevWorldPos), Shape.MINIMUM_SCALE);
        transform.setScale(scale);

        transformObj.setScale(scale);
        return object;
    }

    public GameObject moveObject(@NotNull GameObject object) { return moveObject(object, new Vector2f()); }
    public GameObject moveObject(@NotNull GameObject object, Vector2f offset) {
        Transform2D<Vector2f> transformComponent = object.getComponent(Transform2D.class);
        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());

        if (transformComponent != null) {
            Vector2f prevPos = worldPos.sub(offset);
            transformComponent.setPosition(prevPos);
            InputShapesEvents.setPosCallback(prevPos);
        }
        return object;
    }

    public void deleteSelected() {
        if (currentObject == null) return;
        actionHandler.perform(new ActionDelete(currentObject));
        currentObject = null;
    }
    public void copySelected() {
        copiedObject = currentObject.copy();
    }
    public void cutSelected() {
        if (currentObject == null) return;
        copySelected();
        actionHandler.perform(new ActionDelete(currentObject));
    }
    public void pasteObject() {
        if (copiedObject == null || copiedObject.getClass() != Shape.class) return;
        Shape shape = (Shape) copiedObject.copy();
        Transform2D<Vector2f> transform = shape.getComponent(Transform2D.class);
        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());

        if (transform != null) transform.setPosition(worldPos);
        // shape = shape.copy();

        actionHandler.perform(new ActionStamp(sceneManager.getScene(), shape));
        setCurrentObject(shape);
    }

    public void setSelected(GameObject object) { currentObject = object; InputShapesEvents.setSelectedCallback(object); }
    public void setMouseEngaged(boolean engaged) {this.mouseEngaged = engaged;}
    public void setActiveTransform(boolean active) {
        this.activeTransform = active;
        if (!active) return;
        transform.setPosition(new Vector2f(WorldCoords.screenToWorld(mousePos, sceneManager.getCamera())));
    }
    public void setCurrentObject(GameObject object) { this.currentObject = object; }
    public void setToolsMode(TOOLS mode) {
        this.toolsMode = mode;
        currentObject = null;
        InputShapesEvents.setToolModeCallback(toolsMode);
    }

    public GameObject getSelected() { return currentObject; }
    public static Vector3f getColourSelected() { return colourSelected; }
    public static TOOLS getToolsMode() { return toolsMode; }
    public boolean hasSelected() { if (currentObject != null) return true; return false;}
}
