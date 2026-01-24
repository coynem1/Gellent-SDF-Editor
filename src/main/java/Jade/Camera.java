package Jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMat, viewMat;
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


    public Camera(Vector2f position) {
        this.projectionMat = new Matrix4f();
        this.viewMat = new Matrix4f();
        this.position = position;
        adjustProjection();
    }

    // Used for scaling screen
    public void adjustProjection() {
        Window window = Window.get();
        float aspectRatio = (float) window.getWidth() / (float) window.getHeight();

        viewWidth  = viewHeight * aspectRatio;

        projectionMat.identity();
        projectionMat.ortho(
                -viewWidth  / 2.0f, viewWidth  / 2.0f,
                -viewHeight / 2.0f, viewHeight / 2.0f,
                NEAR_PLANE, FAR_PLANE
        );
    }

    // Base camera matrix
    public Matrix4f getViewMat() {
        Vector3f camFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMat.identity();
        this.viewMat = viewMat.lookAt(
                new Vector3f(position.x, position.y, CAMERA_Z),     // Camera location
                camFront.add(position.x, position.y, 0.0f),     // Camera viewing center
                camUp                                              // Up
        );
        return this.viewMat;
    }

    // Window screen matrix
    public Matrix4f getProjectionMat() {
        return this.projectionMat;
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public float getViewWidth() {return this.viewWidth;}
    public float getViewHeight() {return this.viewHeight;}


}
