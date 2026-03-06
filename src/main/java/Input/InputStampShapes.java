package Input;

import Jade.Camera;
import Jade.Scene;
import Jade.SceneManager;
import Jade.Window;
import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import util.Transform2D;
import util.WorldCoords;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class InputStampShapes {
    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    public enum SHAPES { CIRCLE, BOX, TRIANGLE, HEXAGON};
    public static final HashMap<SHAPES, String> SHAPE_NAMES = new HashMap<>() {{
        put(SHAPES.CIRCLE, "Circle");
        put(SHAPES.BOX, "Box");
        put(SHAPES.TRIANGLE, "Triangle");
        put(SHAPES.HEXAGON, "Hexagon");
    }};

    private Transform2D<Vector2f> transform = Transform2D.createFloat();
    private SculptObject currentSculpt = new SculptObject();
    private Scene currentScene;
    private Vector2f mousePos = new Vector2f();
    private SHAPES selectedShape = SHAPES.CIRCLE;

    private ArrayList<Shape> shapeList = new ArrayList<>();
    private boolean editMode = true;
    private Camera camera;


    public InputStampShapes(Scene scene) {
        this.currentScene = scene;
        this.camera = scene.getCamera();

        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onColourChanged((colour) -> {
            if (!editMode) { return; }
            colourSelected = colour;
        });
        InputImGui.onScaleChanged((scale) -> {
            if (!editMode) { return; }
            transform.setScale(scale);
        });
        InputImGui.onRotationChanged((rotation) -> {
            if (!editMode) { return; }
            transform.setRotation(rotation);
        });
        InputImGui.onShapeChanged((shape) -> {
            if (!editMode) { return; }
            selectedShape = shape;
        });

        InputMouseEvents.onBtnPressed((button, _) -> {
            if (!editMode) { return; }
            switch (button) {
                // Change Scene
                case GLFW_MOUSE_BUTTON_LEFT:
                    stampShape();
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            if (!editMode) { return; }
            mousePos = new Vector2f(xPos, yPos);
            Vector2f worldPos = WorldCoords.screenToWorld(mousePos, camera);
            transform.setPosition(worldPos);
        });
    }

    private void stampShape() {
        Transform2D<Vector2f> copyTransform = transform.copy(); // Shouldn't be a reference
        Shape object = new Shape(selectedShape, currentSculpt);

        object.setTransform(copyTransform);
        object.setColour(colourSelected);

        // Debugging output
        // IO.println("Pos: "+ object.getTransform().getPosition());
        // IO.println("Rot: "+ object.getTransform().getRotation());
        // IO.println("Scale: "+ object.getTransform().getScale() + "\n");

        shapeList.add(object);
        currentScene.addObjectToScene(object);
    }

    public static Vector3f getColourSelected() { return colourSelected; }
}
