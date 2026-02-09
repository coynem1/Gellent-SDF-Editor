package Jade;

import Input.InputCamera;
import Input.InputWindowEvents;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static java.lang.Float.max;

public class Camera {
    private Matrix4f projectionMat, staticProjectionMat, viewMat, staticViewMat;
    private Vector2f position;

    // TODO: Make these dynamic to changing aspect ratio
    // Rendering
    public static final float TILE_SIZE = 10.0f;
    public static final int TILE_COUNT_Y = 21;  // Not resolution based, always This many tiles

    // Camera
    public static final float CAMERA_Z = 50.0f;     // In focus plane
    public static final float NEAR_PLANE = 0.0f;
    public static final float FAR_PLANE  = 100.0f;

    private float viewHeight = TILE_SIZE * TILE_COUNT_Y;
    private float viewWidth;
    private float zoom = 1.0f;

    private int windowWidth = 1;
    private int windowHeight = 1;
    // private float initialAspectRatio = 1.0f;

    // Input
    private InputCamera inputCamera;


    public Camera(Vector2f position) {
        this.projectionMat = new Matrix4f();
        this.staticProjectionMat = new Matrix4f();
        this.viewMat = new Matrix4f();
        this.staticViewMat = new Matrix4f();
        this.position = position;
        this.inputCamera = new InputCamera(this);
        this.windowWidth = Window.get().getWidth();
        this.windowHeight = Window.get().getHeight();
        // this.initialAspectRatio = (float) windowWidth / (float) windowHeight;

        this.inputCamera.bindInputs();
        // calculateTile();

        adjustProjection(projectionMat, false);
        adjustProjection(staticProjectionMat, true);

        // // Auto update if windows resized
        // InputWindowEvents.onWindowResized((newW, newH) -> {
        //     windowWidth = newW;
        //     windowHeight = newH;
        // });
    }

    private void calculateTile() {
        // windowHeight /
    }


    public void process() {
        this.inputCamera.process();
    }

    // Calculates projection matrix screen from the camera or for
    public void adjustProjection(Matrix4f projectionMatrix, boolean isStatic) {
        final float ZOOM_MIN = 0.001f;
        float safeZoom = Math.max(zoom, ZOOM_MIN);
        float aspectRatio = (float) windowWidth / (float) windowHeight;

        viewWidth  = viewHeight * aspectRatio;

        // Remove zoom if static
        if (isStatic) { safeZoom = 1.0f;}

        // Centered and scaled for zoom
        float halfWidth = (viewWidth / 2.0f) / safeZoom;
        float halfHeight = (viewHeight / 2.0f) / safeZoom;

        projectionMatrix.identity().ortho(
            -halfWidth, halfWidth,
            -halfHeight, halfHeight,
            NEAR_PLANE, FAR_PLANE
        );

    }

    // Base camera matrix
    public Matrix4f getViewMat(boolean isStatic) {
        Vector3f camFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector2f pos = this.position;

        if (isStatic) { pos = new Vector2f(0.0f, 0.0f);}

        this.viewMat.identity();
        this.viewMat = viewMat.lookAt(
                new Vector3f(pos.x, pos.y, CAMERA_Z),    // Camera location
                camFront.add(pos.x, pos.y, 0.0f),     // Camera viewing center
                camUp                                    // Up
        );
        return this.viewMat;
    }

    // UI screen matrix
    public Matrix4f getProjectionMat() {
        return this.projectionMat;
    }

    // SDF screen matrix
    public Matrix4f getStaticProjectionMat() {
        return this.staticProjectionMat;
    }


    public Vector2f getPosition() {
        return this.position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public float getViewWidth() {return this.viewWidth;}
    public float getViewHeight() {return this.viewHeight;}

    public void setZoom(float zoom) {
        final float MIN_ZOOM = 0.001f;
        this.zoom = max(zoom, MIN_ZOOM);
        adjustProjection(projectionMat, false);
    }

    public float getZoom() { return this.zoom;}


}
