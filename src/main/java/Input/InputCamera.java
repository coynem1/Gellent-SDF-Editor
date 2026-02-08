package Input;

import Jade.Camera;
import Jade.MouseListener;
import Jade.Window;
import org.joml.Vector2f;

import static java.lang.Double.min;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

// Camera movement inputs
public class InputCamera {
    private Camera camera;
    private Window window;

    private boolean middleMouseHeld;
    private Vector2f startCameraPos;
    private Vector2f screenAnchor;
    private float zoom;

    public InputCamera(Camera camera){
        this.camera = camera;
        this.middleMouseHeld = false;
        this.screenAnchor = null;
        this.window = Window.get();
        this.zoom = 1.0f;
    }

    // Bind editor buttons
    public void bindInputs() {
        final double SCROLL_SCALAR = 0.1f;

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

        MouseListener.onScroll((xOffset, yOffset) -> {
            // Change zoom level
            zoom = Math.max(zoom + (float) (yOffset * SCROLL_SCALAR), 0.0f);
            IO.println("Zoom: " + zoom);
            camera.setZoom(zoom);

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
        Vector2f currentMouse = new Vector2f(MouseListener.getXY());
        Vector2f distPixels = currentMouse.sub(screenAnchor, currentMouse);   // Distance between anchor and current mouse

        // Convert pixel distance to world distance
        float unitsX = camera.getViewWidth() / (float) window.getWidth();
        float unitsY = camera.getViewHeight() / (float) window.getHeight();

        Vector2f distWorld = new Vector2f(
            distPixels.x * unitsX,
            -distPixels.y * unitsY   // flip Y
        );

        // Dragging right should move the camera left (so the world appears to follow your hand)
        camera.setPosition(new Vector2f(startCameraPos).sub(distWorld));
    }



}
