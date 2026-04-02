package Input.Actions;

import Rendering.Objects.GameObject;
import org.joml.Vector2f;
import Rendering.Objects.Components.Transform2D;

public class ActionRotate implements Action {
    GameObject object;
    float angleA, angleB;

    public ActionRotate(GameObject object, float angleA, float angleB) {
        this.object = object;
        this.angleA = angleA;
        this.angleB = angleB;
    }

    @Override public void execute() {
        Transform2D<Vector2f> transform = object.getComponent(Transform2D.class);
        if (transform == null) return;

        transform.setRotation(angleB);
    }
    @Override public void undo() {
        Transform2D<Vector2f> transform = object.getComponent(Transform2D.class);
        if (transform == null) return;

        transform.setRotation(angleA);
    }
}
