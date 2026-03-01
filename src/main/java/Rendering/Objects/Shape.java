package Rendering.Objects;

import Input.InputStampShapes;
import Rendering.Objects.Components.ComponentTransform;
import org.joml.Vector3f;

public abstract class Shape extends GameObject {
    public static final String DEFAULT_NAME = "Shape";
    protected SculptObject sculptObject;
    protected Vector3f colour = InputStampShapes.SELECTED_COLOUR;

    public Shape(String name) {
        super(name);

        // Add components
        addComponent(new ComponentTransform());
    }

    // Setters for tweaking
    public void setColour(Vector3f colour) {this.colour = colour;}

}
