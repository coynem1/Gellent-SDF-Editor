package Jade;

import Observers.InputKeyEvents;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;

public class SceneBase extends Scene {
    private static final int RENDER_MODES_LEN = 4;

    public SceneBase() {
        super();

        bindInputs();
    }

    // Single binding when key changes
    private void bindInputs() {
        InputKeyEvents.onKeyPressed((key, scancode, mods) -> {
            switch (key) {
                case GLFW_KEY_SPACE:
                    toggleRender = (toggleRender + 1) % RENDER_MODES_LEN;
                    break;
            }
        });
    }


}
