package Input;

import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.Components.ComponentBox;
import Rendering.Objects.Shape;
import Rendering.Objects.Components.ComponentCircle;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class InputStampShapes {
    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    private float rotation = 0f;
    private float scale = 1.0f;
    private Vector2i mousePos = new Vector2i();

    public enum shapes { CIRCLE, BOX, TRIANGLE, HEXAGON};
    private shapes selectedShape = shapes.CIRCLE;

    private ArrayList<Shape> shapeList = new ArrayList<>();
    private boolean editMode = false;


    public InputStampShapes() {
        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onColourChanged((colour) -> {
            colourSelected = colour;
        });

        // InputKeyEvents.onKeyPressed((key, _, _) -> {
        //     switch (key) {
        //         // Test
        //         case GLFW_KEY_0:
        //             editMode = true;
        //             break;
        //     }
        // });

        InputMouseEvents.onBtnPressed((button, _) -> {
            switch (button) {
                // Change Scene
                case GLFW_MOUSE_BUTTON_LEFT:
                    IO.println("Stamping");
                    stampShape();
                    break;
            }
        });

        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            mousePos = new Vector2i(xPos, yPos);
        });
    }

    private void stampShape() {
        Shape object = new Shape(Shape.DEFAULT_NAME, shapes.CIRCLE);
        Vector2i worldPos = new Vector2i(mousePos);

        object.setPosition(worldPos);
        object.setRotation(rotation);
        object.setScale(scale);
        object.setColour(colourSelected);

        // Debugging output
        IO.println("Pos: "+ object.getTransform().getPosition() + "\n");
        IO.println("Rot: "+ object.getTransform().getRotation() + "\n");
        IO.println("Scale: "+ object.getTransform().getScale() + "\n");

        object.start();
        shapeList.add(object);
    }

    public static Vector3f getColourSelected() { return colourSelected; }
}
