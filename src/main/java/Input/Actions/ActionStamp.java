package Input.Actions;

import Jade.Scene;
import Rendering.Objects.Shape;
import java.util.List;

public class ActionStamp implements Action {
    private final Shape shape;
    private final Scene scene;

    public ActionStamp(Scene scene, Shape shape) {
        this.scene = scene;
        this.shape = shape;
    }

    @Override public void execute() { scene.addObjectToScene(shape); }
    @Override public void undo() { scene.removeObjectFromScene(shape); }
}
