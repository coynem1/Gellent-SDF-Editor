package util;

import Jade.Camera;
import Jade.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;

public abstract class WorldCoords {

    // Calculates screen co-ordinates to world co-ordinates
    public static Vector2f screenToWorld(Vector2f point, Camera camera) {
        Vector2f windowSize = new Vector2f(Window.get().getWidth(), Window.get().getHeight());
        Vector2f camPos = camera.getPosition();

        // Convert to Normalised Device Co-ordinates
        Vector2f ndc = new Vector2f(
            (point.x / windowSize.x) * 2.0f - 1.0f,
            -((point.y / windowSize.y)) * 2.0f + 1.0f
        );

        float aspectRatio = (float) windowSize.x / windowSize.y;
        float viewWidth = camera.getViewHeight() * aspectRatio;

        double halfWidth = (viewWidth / 2.0) / camera.getZoom();
        double halfHeight = (camera.getViewHeight() / 2.0) / camera.getZoom();

        // Scale NDC to world space and offset by camera position
        Vector2f worldPos = new Vector2f(
            (float) (ndc.x * halfWidth  + camPos.x),
            (float) (ndc.y * halfHeight + camPos.y)
        );

        return worldPos;
    }
}
