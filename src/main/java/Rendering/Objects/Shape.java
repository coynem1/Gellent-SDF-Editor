package Rendering.Objects;

import Input.InputShapes;
import Rendering.Objects.Components.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import Rendering.Objects.Components.Transform2D;

public class Shape extends GameObject {
    public transient static final float MINIMUM_SCALE = 0.1f;   // Cannot scale to zero

    protected SculptObject sculptObject;
    protected Vector3f colour = InputShapes.getColourSelected();
    protected InputShapes.SHAPES shapeType = null;
    protected InputShapes.MODES shapeModes = InputShapes.MODES.UNION;

    // Default shape constructor
    public Shape(InputShapes.SHAPES shape, SculptObject sculpt) {
        super();
        sculptObject = sculpt;

        setShape(shape);
    }

    // Setters for tweaking
    public void setColour(Vector3f colour) {this.colour = colour;}
    public void setTransform(Transform2D<Vector2f> newTransform) {
        Transform2D<Vector2f> transform = getComponent(Transform2D.class);

        // Remove transform if it's empty
        if (newTransform == null) {
            if (transform == null) return;
            removeComponents(Transform2D.class);
            return;
        }

        // Create a transform component if needed and set it
        if (transform == null) {
            addComponent(newTransform.copy());
            return;
        }

        transform.copyFrom(newTransform);
    }
    public void setBlend(float blend) {
        Blending blending = getComponent(Blending.class);

        // Remove blending if it's zero
        if (blend == 0f) {
            if (blending == null) return;
            removeComponents(Blending.class);
            return;
        }

        // Create a blending component if needed and set it
        if (blending == null) {
            blending = new Blending();
            blending.setBlend(blend);
            addComponent(blending);
            return;
        }

        blending.setBlend(blend);
    }
    public void setRounded(float round) {
        ComponentRounded rounded = getComponent(ComponentRounded.class);

        // Remove rounded if it's zero
        if (round == 0f) {
            if (rounded == null) return;
            removeComponents(ComponentRounded.class);
            return;
        }

        // Create a rounded component if needed and set it
        if (rounded == null) {
            // Check if the shapes unroundable
            for (var unroundable : InputShapes.UNROUNDABLE_SHAPES) {
                if (this.getShapeType() == unroundable) return;
            }

            rounded = new ComponentRounded();
            rounded.setRounded(round);
            addComponent(rounded);
            return;
        }

        rounded.setRounded(round);
    }
    public void setShapeMode(InputShapes.MODES shapeMode) {this.shapeModes = shapeMode;}
    public void setShape(@NotNull InputShapes.SHAPES shape) {
        shapeType = shape;

        // if unroundable, remove the rounded component that might be there
        if (InputShapes.UNROUNDABLE_SHAPES.contains(shape)) {
            removeComponents(ComponentRounded.class);
        }
    }

    // Getters
    public Vector3f getColour() {return this.colour;}
    public Transform2D<Vector2f> getTransform() {
        Transform2D<Vector2f> transform = getComponent(Transform2D.class);

        if (transform != null) {
            return transform;
        };
        return Transform2D.createFloat();
    }
    public InputShapes.SHAPES getShapeType() {return this.shapeType;}
    public float getBlend() {
        Blending blending = getComponent(Blending.class);

        // Remove blending if its zero
        if (blending != null) {
            if (blending.getBlend() > 0f) return blending.getBlend();
            else removeComponents(Blending.class);
        };
        return 0f;
    }
    public float getRounded() {
        ComponentRounded rounded = getComponent(ComponentRounded.class);

        // Remove rounded if it's zero
        if (rounded != null) {
            if (rounded.getRounded() > 0f) return rounded.getRounded();
            else removeComponents(ComponentRounded.class);
        };
        return 0f;
    }
    public int getShapeMode() {return shapeModes.ordinal();}

    @Override
    public Shape copy() {
        Shape clone = new Shape(getShapeType(), sculptObject);

        for (Component c : getComponents()) {
            clone.addComponent(c.copy());
        }

        clone.colour = colour;
        clone.shapeType = shapeType;
        clone.shapeModes = shapeModes;

        return clone;
    }
}
