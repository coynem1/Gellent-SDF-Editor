package Rendering.Objects.Components;

// Base for all shapes
public interface BasicShape {
    static final String DEFAULT_NAME = "Shape";
    static final float DEFAULT_LENGTH = 1.0f;

    abstract void setDimensions(float width, float height);
}
