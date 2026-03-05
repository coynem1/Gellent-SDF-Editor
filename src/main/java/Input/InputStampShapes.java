package Input;

import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.Components.ComponentBox;
import Rendering.Objects.Shape;
import Rendering.Objects.Components.ComponentCircle;
import org.joml.Vector2i;
import org.joml.Vector3f;
import util.Transform2D;

import java.util.ArrayList;
import java.util.Arrays;
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

    private Transform2D transform = new Transform2D();
    private Vector2i mousePos = new Vector2i();
    private SHAPES selectedShape = SHAPES.CIRCLE;

    private ArrayList<Shape> shapeList = new ArrayList<>();
    private boolean editMode = false;


    public InputStampShapes() {
        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onColourChanged((colour) -> {
            colourSelected = colour;
        });
        InputImGui.onScaleChanged((scale) -> {
            transform.setScale(scale);
        });
        InputImGui.onRotationChanged((rotation) -> {
            transform.setRotation(rotation);
        });
        InputImGui.onShapeChanged((shape) -> {
            selectedShape = shape;
        });

        InputMouseEvents.onBtnPressed((button, _) -> {
            switch (button) {
                // Change Scene
                case GLFW_MOUSE_BUTTON_LEFT:
                    stampShape();
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            mousePos = new Vector2i(xPos, yPos);
            Vector2i worldPos = new Vector2i(mousePos);
            transform.setPosition(mousePos);
        });
    }

    private void stampShape() {
        Shape object = new Shape(Shape.DEFAULT_NAME, selectedShape);

        object.setTransform(transform);
        object.setColour(colourSelected);

        // Debugging output
        // IO.println("Pos: "+ object.getTransform().getPosition());
        // IO.println("Rot: "+ object.getTransform().getRotation());
        // IO.println("Scale: "+ object.getTransform().getScale() + "\n");

        object.start();
        shapeList.add(object);
    }

    public static Vector3f getColourSelected() { return colourSelected; }
}
