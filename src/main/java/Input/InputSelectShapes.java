package Input;

import Input.Actions.*;
import Jade.SceneManager;
import Observers.InputImGui;
import Observers.InputKeyEvents;
import Observers.InputMouseEvents;
import Observers.InputShapesEvents;
import Rendering.Objects.Components.*;
import Rendering.Objects.GameObject;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.joml.Vector2f;
import util.CalculateSDF;
import util.WorldCoords;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class InputSelectShapes {
    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private GameObject selectedObjectBefore = null;

    private SceneManager sceneManager;
    private ActionHandler actionHandler;
    private InputShapes inputShapes;

    private Vector2f mousePos = new Vector2f();
    private boolean shortcutsUsed[] = new boolean[InputShapes.SHORTCUTS.values().length];
    private boolean usingShortcuts = true; // Active shape tracks mouse position
    private boolean moving = false;
    private boolean activeImGui[] = new boolean[InputShapes.SHORTCUTS.values().length];   // If actively editing through ImGui
    private boolean enabled = false;

    public InputSelectShapes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void init(InputShapes inputShapes) {
        this.inputShapes = inputShapes;
        this.actionHandler = sceneManager.getActionHandler();

        updateActiveShape();
        bindInputs();
    }

    // Set everything back to default, for new scenes
    public void reset() {
        selectedObjectBefore = null;
        moving = false;
        transform = Transform2D.createFloat();
        usingShortcuts = true;
        shortcutsUsed = new boolean[InputShapes.SHORTCUTS.values().length];
        enabled = (inputShapes.getToolsMode() == InputShapes.TOOLS.SELECT);
    }

    private void bindInputs() {
        // ImGui UI inputs
        InputImGui.onScaleChanged((scale, pressed) -> {
            InputShapes.SHORTCUTS sc = InputShapes.SHORTCUTS.SCALE;
            Transform2D<Vector2f> transformComponent = imGuiEditing(Transform2D.class, sc, pressed);

            if (transformComponent != null) transformComponent.setScale(scale);

        });
        InputImGui.onBlendChanged((blend, pressed) -> {
            InputShapes.SHORTCUTS sc = InputShapes.SHORTCUTS.BLEND;
            imGuiEditing(Blending.class, sc, pressed);

            inputShapes.blendShortcut(inputShapes.getSelected(), blend);
        });
        InputImGui.onRoundChanged((round, pressed) -> {
            InputShapes.SHORTCUTS sc = InputShapes.SHORTCUTS.ROUND;
            imGuiEditing(ComponentRounded.class, sc, pressed);

            inputShapes.roundShortcut(inputShapes.getSelected(), round);
        });
        InputImGui.onRotationChanged((rotation, pressed) -> {
            InputShapes.SHORTCUTS sc = InputShapes.SHORTCUTS.ROTATE;
            Transform2D<Vector2f> transformComponent = imGuiEditing(Transform2D.class, sc, pressed);

            float radians = (float) Math.toRadians(rotation);
            if (transformComponent != null) transformComponent.setRotation(radians);
        });
        InputImGui.onPosChanged((pos, pressed) ->{
            InputShapes.SHORTCUTS sc = InputShapes.SHORTCUTS.MOVE;
            Transform2D<Vector2f> transformComponent = imGuiEditing(Transform2D.class, sc, pressed);

            if (transformComponent != null) transformComponent.setPosition(pos);
        });


        InputImGui.onShapeChanged((shape) -> {
            if (!enabled) return;
            GameObject selectedObject = inputShapes.getSelected();

            // Check it's a shape
            if (selectedObject == null || selectedObject.getClass() != Shape.class) return;
            Shape shapeObj = (Shape) selectedObject;
            InputShapes.SHAPES shapeTypeBefore = shapeObj.getShapeType();

            inputShapes.changeShape(shapeObj, shape);
            actionHandler.perform(new ActionSetShape(shapeObj, shapeTypeBefore, shapeObj.getShapeType()));
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
            GameObject selectedObject;

            // Grab hovered shape
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    selectShape();
                    selectedObject = inputShapes.getSelected();
                    if (selectedObject == null) return;
                    moving = true;

                    transformComponent = selectedObject.getComponent(Transform2D.class);
                    if (transformComponent == null) return;

                    savePreviousObject(InputShapes.SHORTCUTS.MOVE);
                    Transform2D<Vector2f> transformBefore = selectedObjectBefore.getComponent(Transform2D.class);
                    if (transformBefore == null) return;

                    transform.setPosition(worldPos.sub(transformBefore.getPosition(), new Vector2f()));
                    moveSelected();
                    break;
            }
        });
        InputMouseEvents.onBtnReleased((button, _) -> {
            // If focused on UI or not enabled, ignore
            if (ImGui.getIO().getWantCaptureMouse() || !enabled) { return; }

            if (inputShapes.getSelected() == null) return;

            // Grab hovered shape
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    moving = false;
                    saveAction(InputShapes.SHORTCUTS.MOVE);
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
            if (moving && !ImGui.getIO().getWantCaptureMouse()) moveSelected();
        });

        // Shortcuts
        InputKeyEvents.onKeyPressed((key, _, mods) -> {
            boolean ctrl  = (mods & GLFW_MOD_CONTROL) != 0;
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
            boolean pressed = true;

            if (!enabled) return;
            GameObject selectedObject = inputShapes.getSelected();


            if (!ctrl && !shift) {
                switch (key) {
                    case GLFW_KEY_DELETE:
                        if (selectedObject == null) return;
                        inputShapes.deleteSelected();
                        break;
                    case GLFW_KEY_BACKSPACE:
                        if (selectedObject.getClass() != Shape.class) return;
                        Shape shape = (Shape) selectedObject;

                        savePreviousObject(InputShapes.SHORTCUTS.MODE);
                        shape.setShapeMode(inputShapes.toggleMode(shape));
                        saveAction(InputShapes.SHORTCUTS.MODE);
                        break;
                    case GLFW_KEY_S:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.SCALE);
                        if (selectedObject == null) return;
                        savePreviousObject(InputShapes.SHORTCUTS.SCALE);
                        inputShapes.scaleShortcut(selectedObject);
                        break;
                    case GLFW_KEY_R:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROTATE);
                        if (selectedObject == null) return;
                        savePreviousObject(InputShapes.SHORTCUTS.ROTATE);
                        inputShapes.rotateShortcut(selectedObject);
                        break;
                    case GLFW_KEY_B:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.BLEND);
                        if (selectedObject == null) return;
                        savePreviousObject(InputShapes.SHORTCUTS.BLEND);
                        inputShapes.blendShortcut(selectedObject);
                        break;
                    case GLFW_KEY_F:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROUND);
                        if (selectedObject == null) return;
                        savePreviousObject(InputShapes.SHORTCUTS.ROUND);
                        inputShapes.roundShortcut(selectedObject);
                        break;
                }
            }
            else if (ctrl) {
                switch (key) {
                    case GLFW_KEY_C:
                        if (selectedObject == null) return;
                        inputShapes.copySelected();
                        break;
                    case GLFW_KEY_X:
                        if (selectedObject == null) return;
                        inputShapes.cutSelected();
                        break;
                }
            }
        });

        // Let go of key
        InputKeyEvents.onKeyReleased((key, _, mods) -> {
            boolean ctrl  = (mods & GLFW_MOD_CONTROL) != 0;
            boolean shift = (mods & GLFW_MOD_SHIFT) != 0;
            boolean pressed = false;
            if (!enabled) return;

            Transform2D<Vector2f> transformShape = null;
            GameObject selectedObject = inputShapes.getSelected();
            if (selectedObject != null) transformShape = selectedObject.getComponent(Transform2D.class);

            if (!ctrl && !shift) {
                switch (key) {
                    case GLFW_KEY_S:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.SCALE);
                        if (transformShape == null) return;
                        saveAction(InputShapes.SHORTCUTS.SCALE);
                        break;
                    case GLFW_KEY_R:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROTATE);
                        if (transformShape == null) return;
                        saveAction(InputShapes.SHORTCUTS.ROTATE);
                        break;
                    case GLFW_KEY_B:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.BLEND);
                        if (transformShape == null) return;
                        saveAction(InputShapes.SHORTCUTS.BLEND);
                        break;
                    case GLFW_KEY_F:
                        freezeActiveShape(pressed, InputShapes.SHORTCUTS.ROUND);
                        if (transformShape == null) return;
                        saveAction(InputShapes.SHORTCUTS.ROUND);
                        break;
                }
            }
        });
    }

    // Update the shortcut, create an action and return the ImGui Component that's being changed
    private <T extends Component> T imGuiEditing(Class<T> t, InputShapes.SHORTCUTS sc, boolean pressed) {
        GameObject selectedObject = inputShapes.getSelected();

        if (selectedObject == null || !enabled) return null;
        T instance = selectedObject.getComponent(t);

        // Only save action when not actively editing through ImGui
        if (!activeImGui[sc.ordinal()]) {
            savePreviousObject(sc);
            activeImGui[sc.ordinal()] = true;
        }
        if (!pressed) {
            saveAction(sc);
            activeImGui[sc.ordinal()] = false;
        }

        return instance;
    }

    // Save the action and its properties
    private void saveAction(InputShapes.SHORTCUTS shortcut) {
        GameObject selectedObject = inputShapes.getSelected();
        Transform2D<Vector2f> transformNow = selectedObject.getComponent(Transform2D.class);
        Transform2D<Vector2f> transformBefore = selectedObjectBefore.getComponent(Transform2D.class);

        switch (shortcut) {
            case MOVE:
                if (transformNow == null) return;
                actionHandler.perform(new ActionMove(selectedObject,
                        new Vector2f(transformBefore.getPosition()),
                        new Vector2f(transformNow.getPosition()))
                );
                break;
            case SCALE:
                if (transformNow == null) return;
                actionHandler.perform(new ActionScale(selectedObject,
                        transformBefore.getScale(),
                        transformNow.getScale())
                );
                break;
            case ROTATE:
                if (transformNow == null) return;
                actionHandler.perform(new ActionRotate(selectedObject,
                        transformBefore.getRotation(),
                        transformNow.getRotation())
                );
                break;
            case BLEND:
                Blending blendingNow = selectedObject.getComponent(Blending.class);
                Blending blendingBefore = selectedObjectBefore.getComponent(Blending.class);

                float blendA = blendingBefore == null ? 0f : blendingBefore.getBlend();
                float blendB = blendingNow == null ? 0f : blendingNow.getBlend();

                actionHandler.perform(new ActionBlend(selectedObject,
                        blendA,
                        blendB)
                );
                break;
            case ROUND:
                ComponentRounded roundingNow = selectedObject.getComponent(ComponentRounded.class);
                ComponentRounded roundingBefore = selectedObjectBefore.getComponent(ComponentRounded.class);

                float roundedA = roundingBefore == null ? 0f : roundingBefore.getRounded();
                float roundedB = roundingNow == null ? 0f : roundingNow.getRounded();

                actionHandler.perform(new ActionRound(selectedObject,
                        roundedA,
                        roundedB)
                );
                break;
            case MODE:
                Shape shapeNow;
                Shape shapeBefore;

                // Check if it's possible to cast to a shape
                try {
                    shapeNow = (Shape) selectedObject;
                    shapeBefore = (Shape) selectedObjectBefore;
                } catch (ClassCastException e) { return; }

                int shapeModeA = shapeBefore.getShapeMode();
                int shapeModeB = shapeNow.getShapeMode();

                actionHandler.perform(new ActionMode(shapeNow,
                        InputShapes.MODES.values()[shapeModeA],
                        InputShapes.MODES.values()[shapeModeB]
                ));
                break;
        }
    }

    private void savePreviousObject(InputShapes.SHORTCUTS shortcut) {
        GameObject selectedObject = inputShapes.getSelected();
        if (selectedObject == null) return;

        // Save the previous object
        if (selectedObjectBefore == null) {
            if (selectedObject.getClass() == Shape.class) {
                selectedObjectBefore = ((Shape) selectedObject).copy();
                return;
            }
            selectedObjectBefore = selectedObject.copy();
            return;
        }

        Transform2D<Vector2f> transformNow = selectedObject.getComponent(Transform2D.class);
        Transform2D<Vector2f> transformBefore = selectedObjectBefore.getComponent(Transform2D.class);

        // Save one part
        switch (shortcut) {
            case MOVE:
                if (transformBefore != null) transformBefore.setPosition(transformNow.getPosition());
                break;
            case SCALE:
                if (transformBefore != null) transformBefore.setScale(transformNow.getScale());
                break;
            case ROTATE:
                if (transformBefore != null) transformBefore.setRotation(transformNow.getRotation());
                break;
            case BLEND:
                Blending blendingNow = selectedObject.getComponent(Blending.class);
                Blending blendingBefore = selectedObjectBefore.getComponent(Blending.class);

                if (blendingNow != null && blendingBefore != null) blendingBefore.setBlend(blendingNow.getBlend());
                else if (selectedObject.getClass() == Shape.class) {
                    ((Shape) selectedObject).setBlend(0);
                    ((Shape) selectedObjectBefore).setBlend(0);
                }
                break;
            case ROUND:
                ComponentRounded roundNow = selectedObject.getComponent(ComponentRounded.class);
                ComponentRounded roundBefore = selectedObjectBefore.getComponent(ComponentRounded.class);

                if (roundNow != null && roundBefore != null) roundBefore.setRounded(roundNow.getRounded());
                else if (selectedObject.getClass() == Shape.class) {
                    ((Shape) selectedObject).setRounded(0);
                    ((Shape) selectedObjectBefore).setRounded(0);
                }
                break;
            case MODE:
                if (selectedObjectBefore.getClass() != Shape.class || selectedObject.getClass() != Shape.class) return;
                Shape shapeNow = (Shape) selectedObject;
                Shape shapeBefore = ((Shape) selectedObjectBefore);

                shapeBefore.setShapeMode(
                        InputShapes.MODES.values()[shapeNow.getShapeMode()]
                );
                break;

        }
    }

    // Select the hovered shape and move it to the mouse position
    private void moveSelected() {
        GameObject selectedObject = inputShapes.getSelected();
        if (selectedObject == null || !enabled) return;

        inputShapes.moveObject(selectedObject, transform.getPosition());
    }

    // Handle when shortcuts are held
    private void shapeShortcut() {
        GameObject selectedObject = inputShapes.getSelected();
        if (selectedObject == null || moving) return;

        if (shortcutsUsed[InputShapes.SHORTCUTS.ROTATE.ordinal()]) { inputShapes.rotateShortcut(selectedObject); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.SCALE.ordinal()]) { inputShapes.scaleShortcut(selectedObject); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.BLEND.ordinal()]) { inputShapes.blendShortcut(selectedObject); }
        if (shortcutsUsed[InputShapes.SHORTCUTS.ROUND.ordinal()]) { inputShapes.roundShortcut(selectedObject); }
    }

    // Selects the shape you hover over before interacting with it
    private void selectShape() {
        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        List<GameObject> objects = sceneManager.getScene().getObjects();
        selectedObjectBefore = null;

        // Find hovered shape
        for (int i = objects.size() - 1; i >= 0; i--) {
            GameObject object = objects.get(i);

            if (object.getClass() != Shape.class) continue;
            if (CalculateSDF.calculateSDF((Shape) object, worldPos) <= 0) {
                inputShapes.setSelected(object);
                inputShapes.setCurrentObject(object);
                return;
            }
        }
        inputShapes.setSelected(null);
        inputShapes.setCurrentObject(null);
    }

    // Stops active shape from changing position
    private boolean freezeActiveShape(boolean pressed, InputShapes.SHORTCUTS shortcut) {
        if (inputShapes.getSelected() == null) return true;

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

    // Changes active shape to the clicked one
    private void updateActiveShape() {
        if (InputShapes.getToolsMode() != InputShapes.TOOLS.SELECT) {
            enabled = false;
            return;
        }
        enabled = true;
    }
}
