package Input;

import Jade.Camera;
import Jade.MouseListener;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

// Camera movement inputs
public class InputCamera {
    private boolean middleMousePressed;
    private Vector2i mouseAnchor;
    private Camera camera;

    public InputCamera(Camera camera){
        this.camera = camera;
        this.middleMousePressed = false;
        this.mouseAnchor = null;
    }

    public void process(){
        cameraMovement();
    }

    public void cameraMovement(){
        cameraAnchor();
    }

    // When the move button is first pressed, set the anchor point
    private void cameraAnchor(){
        middleMousePressed = MouseListener.mouseBtnPress(GLFW_MOUSE_BUTTON_MIDDLE);

        if (middleMousePressed) {
            if (mouseAnchor == null) {
                mouseAnchor = MouseListener.getXY();
                IO.println("Middle Mouse Pressed: " + mouseAnchor);
            }
        }
        else {
            mouseAnchor = null;
            // IO.println("Middle Mouse released: " + mouseAnchor);
        }
    }


}
