package Input.Actions;

import Input.InputShapes;
import Rendering.Objects.Components.Transform2D;
import Rendering.Objects.GameObject;
import Rendering.Objects.Shape;
import org.joml.Vector2f;

public class ActionMode implements Action {
    Shape shape;
    InputShapes.MODES modeA, modeB;

    public ActionMode(Shape shape, InputShapes.MODES modeA, InputShapes.MODES modeB) {
        this.shape = shape;
        this.modeA = modeA;
        this.modeB = modeB;
    }

    @Override public void execute() { shape.setShapeMode(modeB); }
    @Override public void undo() { shape.setShapeMode(modeA); }
}
