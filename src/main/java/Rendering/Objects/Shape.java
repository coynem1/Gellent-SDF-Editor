package Rendering.Objects;

import Input.InputStampShapes;
import Rendering.Objects.Components.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Transform2D;

public class Shape extends GameObject {
    public static final String DEFAULT_NAME = "Shape";
    public static final float MINIMUM_SCALE = 0.001f;   // Cannot scale to zero

    protected SculptObject sculptObject;
    protected Vector3f colour = InputStampShapes.getColourSelected();
    protected Transform2D<Vector2f> transform = Transform2D.createFloat();
    protected Blending blending = new Blending();
    protected InputStampShapes.SHAPES shapeType = null;

    public Shape(InputStampShapes.SHAPES shape, SculptObject sculpt) {
        super();
        // IO.println("Creating shape: " + shape);
        sculptObject = sculpt;
        shapeType = shape;
        transform.setPosition(new Vector2f(0f, 0f));

        addComponent(blending);

        // TODO: Add other shapes
        switch (shape) {
            case CIRCLE:
                addComponent(new ComponentCircle());
                break;
            case BOX:
                addComponent(new ComponentBox());
                break;
            default:
                addComponent(new ComponentCircle());
                IO.println("WARNING: Unrecognised shape type. Defaulting to circle");
                break;
        }
    }

    // Setters for tweaking
    public void setColour(Vector3f colour) {this.colour = colour;}
    public void setTransform(Transform2D transform) {this.transform = transform;}
    public void setBlend(float blend) {this.blending.setBlend(blend);}

    // Getters
    public Vector3f getColour() {return this.colour;}
    public Transform2D getTransform() {return this.transform;}
    public InputStampShapes.SHAPES getShapeType() {return this.shapeType;}
}
