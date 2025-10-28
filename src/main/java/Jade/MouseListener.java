package Jade;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener mouseListener;
    private double scrollX, scrollY;
    private double mouseX, mouseY, lastMouseX, lastMouseY;
    private boolean mouseBtnPressed[] = new boolean[3];
    private boolean dragging = false;

    private MouseListener(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.mouseX = 0.0;
        this.mouseY = 0.0;
        this.lastMouseX = 0.0;
        this.lastMouseY = 0.0;

    }

    public static MouseListener get(){
        if (MouseListener.mouseListener == null){
            MouseListener.mouseListener = new MouseListener();
        }
        return MouseListener.mouseListener;
    }

    static void mousePosCallback(long window, double xpos, double ypos) {
        get().lastMouseX = get().mouseX;
        get().lastMouseY = get().mouseY;
        get().mouseX = xpos;
        get().mouseY = ypos;

    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < get().mouseBtnPressed.length) {
                get().mouseBtnPressed[button] = true;
            }

        } else if (action == GLFW_RELEASE) {
            if (button < get().mouseBtnPressed.length) {
                get().mouseBtnPressed[button] = false;
                get().dragging = false;
            }
        }
    }

}
