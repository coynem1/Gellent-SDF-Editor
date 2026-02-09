package Input;

import Jade.Camera;
import Jade.Window;
import org.joml.Vector2f;

import static java.lang.Math.clamp;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

// Camera movement inputs
public class InputCamera {
    private static final float MIN_ZOOM = 0.05f;
    private static final float MAX_ZOOM = 10.0f;

    private Camera camera;
    private Window window;

    private boolean middleMouseHeld;
    private Vector2f startCameraPos;
    private Vector2f screenAnchor;
    private float zoom;
    private int xPos, yPos;

    public InputCamera(Camera camera){
        this.camera = camera;
        this.middleMouseHeld = false;
        this.screenAnchor = null;
        this.window = Window.get();
        this.zoom = 1.0f;
    }

    // Bind editor buttons
    public void bindInputs() {
        final double SCROLL_SCALAR = 1.1f;

        InputMouseEvents.onBtnPressed((button, mods) -> {
            // Start moving
            if (button == GLFW_MOUSE_BUTTON_MIDDLE) {
                cameraAnchor();
            }
        });
        InputMouseEvents.onBtnReleased((button, mods) -> {
            // Stop moving
            if (button == GLFW_MOUSE_BUTTON_MIDDLE) {
                middleMouseHeld = false;
            }
        });

        InputMouseEvents.onScroll((xOffset, yOffset) -> {
            // Change the zoom level by scroll scalar
            zoom *= (float) Math.pow(SCROLL_SCALAR, yOffset);
            zoom = clamp(zoom, MIN_ZOOM, MAX_ZOOM);
            camera.setZoom(zoom);

        });

        InputMouseEvents.onMove((xPos, yPos, lastXPos, lastYPos) -> {
            // Cache mouse position
            this.xPos = xPos;
            this.yPos = yPos;
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
        screenAnchor = new Vector2f(xPos, yPos);
        middleMouseHeld = true;
    }

    private void cameraMove(){
        Vector2f currentMouse = new Vector2f(xPos, yPos);
        Vector2f distPixels = currentMouse.sub(screenAnchor, currentMouse);   // Distance between anchor and current mouse

        // Convert pixel distance to world distance
        float unitsX = camera.getViewWidth() / zoom / (float) window.getWidth();
        float unitsY = camera.getViewHeight() / zoom / (float) window.getHeight();

        Vector2f distWorld = new Vector2f(
            distPixels.x * unitsX,
            -distPixels.y * unitsY   // flip Y
        );

        // Dragging right should move the camera left (so the world appears to follow your hand)
        camera.setPosition(new Vector2f(startCameraPos).sub(distWorld));
    }



}
