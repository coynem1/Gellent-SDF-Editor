package Jade;

import Input.InputCamera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static java.lang.Float.max;

public class Camera {
    private Matrix4f projectionMat, staticProjectionMat, viewMat, staticViewMat;
    private Vector2f position;

    // Rendering
    public static final float TILE_SIZE = 10.0f;
    public static final int VIEW_TILES_Y = 21;  // Width calculated with aspect ratio

    // Camera
    public static final float CAMERA_Z = 50.0f;     // In focus plane
    public static final float NEAR_PLANE = 0.0f;
    public static final float FAR_PLANE  = 100.0f;

    private final float viewHeight = TILE_SIZE * VIEW_TILES_Y;
    private float viewWidth;
    private float zoom = 1.0f;

    // Input
    private InputCamera inputCamera;

    public Camera(Vector2f position) {
        this.projectionMat = new Matrix4f();
        this.staticProjectionMat = new Matrix4f();
        this.viewMat = new Matrix4f();
        this.staticViewMat = new Matrix4f();
        this.position = position;
        this.inputCamera = new InputCamera(this);

        this.inputCamera.bindInputs();
        adjustProjection(projectionMat, false);
        adjustProjection(staticProjectionMat, true);
    }

    // Calculates projection matrix screen from the camera or for
    public void adjustProjection(Matrix4f projectionMatrix, boolean isStatic) {
        final float ZOOM_MIN = 0.001f;
        float safeZoom = Math.max(zoom, ZOOM_MIN);

        Window window = Window.get();
        float aspectRatio = (float) window.getWidth() / (float) window.getHeight();

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
        Vector2f pos = isStatic ? new Vector2f() : this.position;
        Matrix4f target = isStatic ? this.staticViewMat : this.viewMat;

        target.identity();
        target.lookAt(
                new Vector3f(pos.x, pos.y, CAMERA_Z),
                camFront.add(pos.x, pos.y, 0.0f),
                camUp
        );
        return target;
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
