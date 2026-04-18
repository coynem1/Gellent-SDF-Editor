package Jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {
    @Test
    void CameraViewMatrixIsNotNull() {
        Camera camera = new Camera(new Vector2f());

        assertNotNull(camera.getViewMat(true), "Static View matrix is null");
        assertNotNull(camera.getViewMat(false), "Non-Static View matrix is null");
    }

    @Test
    void ProjectionMatrixChangesOnZoom() {
        Camera camera = new Camera(new Vector2f());

        Matrix4f projectionMatrix = new Matrix4f(camera.getProjectionMat());
        assertNotNull(projectionMatrix, "Projection matrix is null");
        camera.setZoom(2.0f);
        assertNotSame(projectionMatrix, camera.getProjectionMat(), "Projection matrix has not changed after zooming");
    }
}