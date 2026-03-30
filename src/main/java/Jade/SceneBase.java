package Jade;

import Observers.InputKeyEvents;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;

public class SceneBase extends Scene {

    public SceneBase() {
        super();

        bindInputs();
    }

    // TODO: Move to another class
    // Single binding when key changes
    private void bindInputs() {
        InputKeyEvents.onKeyPressed((key, scancode, mods) -> {
            switch (key) {
                // Change render mode
                case GLFW_KEY_LEFT:
                    toggleRender = 0;
                    break;
                case GLFW_KEY_RIGHT:
                    toggleRender = 1;
                    break;
                case GLFW_KEY_SPACE:
                    toggleRender = 2;
                    break;
                case GLFW_KEY_G:
                    toggleRender = 3;
                    break;
            }
        });
    }


}
