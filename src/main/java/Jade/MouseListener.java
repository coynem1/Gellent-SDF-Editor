package Jade;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
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
        if (MouseListener.instance == null){
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    static void mousePosCallback(long window, double xpos, double ypos) {
        get().lastMouseX = get().mouseX;
        get().lastMouseY = get().mouseY;
        get().mouseX = xpos;
        get().mouseY = ypos;
        get().dragging = get().mouseBtnPressed[0] || get().mouseBtnPressed[1] || get().mouseBtnPressed[2];

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

    public static void mouseScrollCallback(long window, double xpos, double ypos) {
        get().mouseX += xpos;
        get().mouseY += ypos;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastMouseX = 0.0;
        get().lastMouseY = 0.0;
    }

    // Returns current mouse pos
    public static float getX() {
        return (float)get().mouseX;
    }
    public static float getY() {
        return (float)get().mouseY;
    }

    // Gets elapsed distance in current frame
    public static float getDx() {
        return (float)(get().lastMouseX  - get().mouseX);
    }
    public static float getDy() {
        return (float)(get().lastMouseY  - get().mouseY);
    }

    // Gets elapsed distance in current frame
    public static float getScrollx() {
        return (float)get().scrollX;
    }
    public static float getScrolly() {
        return (float)get().scrollY;
    }

    public static boolean isDragging() {
        return get().dragging;
    }

    // Check if a certain buttons pressed
    public static boolean mouseBtnPress(int button) {
        if  (button < get().mouseBtnPressed.length) {
            return get().mouseBtnPressed[button];
        }
        return false;
    }


}
