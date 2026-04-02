package Input;

import Input.Actions.*;
import Jade.SceneManager;
import Observers.InputImGui;
import Observers.InputKeyEvents;
import Observers.InputMouseEvents;
import Observers.InputShapesEvents;
import Rendering.Objects.Components.Blending;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GameObject;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import imgui.ImGui;
import org.joml.Vector2f;
import util.CalculateSDF;
import Rendering.Objects.Components.Transform2D;
import util.WorldCoords;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class InputSelectShapes {
    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private SculptObject selectedSculpt = new SculptObject();
    private Shape selectedShape = null;
    private GameObject selectedObject = null;
    private GameObject selectedObjectBefore = null;

    private SceneManager sceneManager;
    private ActionHandler actionHandler;
    private InputShapes inputShapes;

    private Vector2f mousePos = new Vector2f();
    // private Transform2D<Vector2f> prevTransform = Transform2D.createFloat();
    private boolean shortcutsUsed[] = new boolean[InputShapes.SHORTCUTS.values().length];
    private boolean usingShortcuts = true; // Active shape tracks mouse position
    private boolean moving = false;
    private boolean enabled = false;

    public InputSelectShapes(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void init(InputShapes inputShapes) {
        this.inputShapes = inputShapes;
        this.actionHandler = sceneManager.getActionHandler();
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

            if (selectedObject == null) return;

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
                        if (selectedObject.getClass() != Shape.class) return;
                        Shape shape = (Shape) selectedObject;

                        savePreviousObject(InputShapes.SHORTCUTS.MODE);
                        shape.setShapeMode(inputShapes.toggleMode(shape));
                        saveAction(InputShapes.SHORTCUTS.MODE);

                        selectedObject = shape;
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
        });
    }

    // Save the action and its properties
    private void saveAction(InputShapes.SHORTCUTS shortcut) {
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

                // TODO: Problem because blending components are deleted if 0
                if (blendingNow == null || blendingBefore == null) return;

                actionHandler.perform(new ActionBlend(selectedObject,
                        blendA,
                        blendB)
                );
                break;
            case ROUND:
                ComponentRounded roundingNow = selectedObject.getComponent(ComponentRounded.class);
                ComponentRounded roundingBefore = selectedObjectBefore.getComponent(ComponentRounded.class);

                if (roundingNow == null || roundingBefore == null) return;
                actionHandler.perform(new ActionRound(selectedObject,
                        roundingBefore.getRounded(),
                        roundingNow.getRounded())
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
                break;
            case ROUND:
                ComponentRounded roundNow = selectedObject.getComponent(ComponentRounded.class);
                ComponentRounded roundBefore = selectedObjectBefore.getComponent(ComponentRounded.class);

                if (roundNow != null && roundBefore != null) roundBefore.setRounded(roundNow.getRounded());
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

    // Selects the shape you hover over before interacting with it
    private void selectShape() {
        Vector2f worldPos = WorldCoords.screenToWorld(mousePos, sceneManager.getCamera());
        selectedObjectBefore = null;

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
            return;
        }
        enabled = true;
    }
}
