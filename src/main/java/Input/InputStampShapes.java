package Input;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class InputStampShapes {
    public static Vector3f SELECTED_COLOUR = new Vector3f(1.0f, 0.6f, 0.3f);    // Default colour
    private boolean editMode = false;


    public InputStampShapes() {
        bindInputs();
    }

    private void bindInputs() {
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
}
