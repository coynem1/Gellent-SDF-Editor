package Jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMat, viewMat;
    private Vector2f position;

    public Camera(Vector2f position) {
        this.projectionMat = new Matrix4f();
        this.viewMat = new Matrix4f();
        this.position = position;
        adjustProjection();
    }

    // Used for scaling screen
    public void adjustProjection() {
        projectionMat.identity();
        projectionMat.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
    }

    // Base camera matrix
    public Matrix4f getViewMat() {
        Vector3f camFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMat.identity();
        this.viewMat = viewMat.lookAt(
                new Vector3f(position.x, position.y, 50.0f),   // Camera location
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


}
