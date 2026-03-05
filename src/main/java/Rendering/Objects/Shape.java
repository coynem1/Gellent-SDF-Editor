package Rendering.Objects;

import Input.InputStampShapes;
import Rendering.Objects.Components.ComponentBox;
import Rendering.Objects.Components.ComponentCircle;
import Rendering.Objects.Components.MouseFollow;
import org.joml.Vector2i;
import org.joml.Vector3f;
import util.Transform2D;

import static Input.InputStampShapes.SHAPES.*;

public class Shape extends GameObject {
    public static final String DEFAULT_NAME = "Shape";
    public static final float MINIMUM_SCALE = 0.001f;   // Cannot scale to zero

    protected SculptObject sculptObject;
    protected Vector3f colour = InputStampShapes.getColourSelected();
    protected Transform2D transform = new Transform2D();

    public Shape(String name, InputStampShapes.SHAPES shape) {
        super(name);

        IO.println("Creating shape: " + shape);

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
    public void setPosition(Vector2i position) {this.transform.setPosition(position);}
    public void setRotation(float rotation) {this.transform.setRotation(rotation);}
    public void setScale(float scale) {this.transform.setScale(scale);}

    // Getters
    public Vector3f getColour() {return this.colour;}
    public Transform2D getTransform() {return this.transform;}
}
