package Rendering.Objects;

import Input.InputStampShapes;
import Rendering.Objects.Components.ComponentShapeTransform;
import Rendering.Objects.Components.ComponentTransform;
import org.joml.Vector3f;

public abstract class Shape extends GameObject {
    public static final String DEFAULT_NAME = "Shape";
    protected SculptObject sculptObject;
    protected Vector3f colour = InputStampShapes.getColourSelected();

    public Shape(String name) {
        super(name);

        // Add components
        addComponent(new ComponentShapeTransform());
    }

    // Setters for tweaking
    public void setColour(Vector3f colour) {this.colour = colour;}

}
