package Input.Actions;

import Rendering.Objects.Components.Transform2D;
import Rendering.Objects.GameObject;
import org.joml.Vector2f;

public class ActionScale implements Action {
    GameObject object;
    float scaleA, scaleB;

    public ActionScale(GameObject object, float scaleA, float scaleB) {
        this.object = object;
        this.scaleA = scaleA;
        this.scaleB = scaleB;
    }

    @Override public void execute() {
        Transform2D<Vector2f> transform = object.getComponent(Transform2D.class);
        if (transform == null) return;

        transform.setScale(scaleB);
    }
    @Override public void undo() {
        Transform2D<Vector2f> transform = object.getComponent(Transform2D.class);
        if (transform == null) return;

        transform.setScale(scaleA);
    }
}
