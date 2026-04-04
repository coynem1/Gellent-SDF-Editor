package Input.Actions;

import Rendering.Objects.GameObject;
import org.joml.Vector2f;
import Rendering.Objects.Components.Transform2D;

public class ActionMove implements Action {
    GameObject object;
    Vector2f positionA, positionB;

    public ActionMove(GameObject object, Vector2f positionA, Vector2f positionB) {
        this.object = object;
        this.positionA = new Vector2f(positionA);
        this.positionB = new Vector2f(positionB);
    }

    @Override public void execute() {
        Transform2D<Vector2f> transform = object.getComponent(Transform2D.class);
        if (transform == null) return;

        transform.setPosition(positionB);
    }
    @Override public void undo() {
        Transform2D<Vector2f> transform = object.getComponent(Transform2D.class);
        if (transform == null) return;

        transform.setPosition(positionA);
    }
}
