package Input;

import Rendering.ImGui.ImGuiEditor;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class InputStampShapes {
    private static Vector3f colourSelected = ImGuiEditor.getColourSelected();    // Default colour
    private boolean editMode = false;


    public InputStampShapes() {
        bindInputs();
    }

    private void bindInputs() {
        InputImGui.onColourChanged((colour) -> {
            colourSelected = colour;
        });

        InputKeyEvents.onKeyPressed((key, _, _) -> {
            switch (key) {
                // Test
                case GLFW_KEY_0:
                    editMode = true;
                    break;
            }
        });

        InputMouseEvents.onBtnPressed((button, _) -> {
            switch (button) {
                // Change Scene
                case GLFW_MOUSE_BUTTON_LEFT:
                    IO.println("Left mouse button pressed");
                    break;
            }
        });
    }

    public static Vector3f getColourSelected() { return colourSelected; }
}
