package Input.Actions;

import Input.InputShapes;
import Rendering.Objects.Components.Transform2D;
import Rendering.Objects.Shape;
import org.joml.Vector2f;

public class ActionSetShape implements Action {
    Shape shape;
    InputShapes.SHAPES shapeTypeA, shapeTypeB;

    public ActionSetShape(Shape shape, InputShapes.SHAPES shapeTypeA, InputShapes.SHAPES shapeTypeB) {
        this.shape = shape;
        this.shapeTypeA = shapeTypeA;
        this.shapeTypeB = shapeTypeB;
    }

    @Override public void execute() {
        shape.setShape(shapeTypeB);
    }
    @Override public void undo() {
        shape.setShape(shapeTypeA);
    }
}
