package Input;

import Rendering.ImGui.ImGuiEditor;
import Rendering.Objects.GameObject;
import Rendering.Objects.Shape;
import Rendering.Objects.ShapeCircle;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class InputStampShapes {
    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    private static Vector2i mousePos = new Vector2i();

    private enum shapes { CIRCLE, BOX, TRIANGLE, HEXAGON};
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
        Shape object = new ShapeCircle(Shape.DEFAULT_NAME);
        object.setColour(colourSelected);
        object.start();

        shapeList.add(object);
    }

    public static Vector3f getColourSelected() { return colourSelected; }
}
