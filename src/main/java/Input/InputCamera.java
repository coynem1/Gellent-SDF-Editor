package Input;

import Jade.Camera;
import Jade.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

// Camera movement inputs
public class InputCamera {
    private boolean middleMouseHeld;
    private Vector2f startCameraPos;
    private Vector2f screenAnchor;
    private Camera camera;

    public InputCamera(Camera camera){
        this.camera = camera;
        this.middleMouseHeld = false;
        this.screenAnchor = null;
    }

    // Bind editor buttons
    public void bindInputs() {
        MouseListener.onBtnPressed((button, mods) -> {
            // Change Scene
            if (button == GLFW_MOUSE_BUTTON_MIDDLE) {
                IO.println("Middle Mouse Pressed");
                cameraAnchor();

            }
        });
        MouseListener.onBtnReleased((button, mods) -> {
            // Change Scene
            if (button == GLFW_MOUSE_BUTTON_MIDDLE) {
                IO.println("Middle Mouse Released");
                middleMouseHeld = false;
            }
        });
    }

    public void process(){
        if (middleMouseHeld){
            cameraMove();
        }
    }

    // When the move button is first pressed, set the anchor point
    private void cameraAnchor(){
        startCameraPos = camera.getPosition();
        screenAnchor = new Vector2f(MouseListener.getXY());
        IO.println("Middle Mouse Co-ords: " + screenAnchor);
        middleMouseHeld = true;
    }

    private void cameraMove(){

        Vector2f sum = (startCameraPos).sub(new Vector2f(MouseListener.getXY()));
        // IO.println("Camera position: " + screenAnchor);
        camera.setPosition(sum.mul(0.04f));
    }


}
