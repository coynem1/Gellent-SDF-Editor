package Rendering.Objects;

import Input.InputStampShapes;
import Rendering.Objects.Components.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Transform2D;

public class Shape extends GameObject {
    public transient static final String DEFAULT_NAME = "Shape";
    public transient static final float MINIMUM_SCALE = 0.001f;   // Cannot scale to zero

    protected SculptObject sculptObject;
    protected Vector3f colour = InputStampShapes.getColourSelected();
    protected Transform2D<Vector2f> transform = Transform2D.createFloat();
    protected Blending blending = new Blending();
    protected transient Class<? extends Component> shapeTypeClass = null;
    protected InputStampShapes.SHAPES shapeType = null;
    protected InputStampShapes.MODES shapeModes = InputStampShapes.MODES.UNION;

    // Default shape constructor
    public Shape(InputStampShapes.SHAPES shape, SculptObject sculpt) {
        super();
        sculptObject = sculpt;
        transform.setPosition(new Vector2f(0f, 0f));

        setShape(shape);
        addComponent(blending);
    }

    // Setters for tweaking
    public void setColour(Vector3f colour) {this.colour = colour;}
    public void setTransform(Transform2D transform) {this.transform = transform;}
    public void setBlend(float blend) {this.blending.setBlend(blend);}
    public void setShapeMode(InputStampShapes.MODES shapeMode) {this.shapeModes = shapeMode;}
    public void setShape(InputStampShapes.SHAPES shape) {
        shapeType = shape;
        if (shapeTypeClass != null) {
            removeComponents(shapeTypeClass);
        }

        switch (shape) {
            case CIRCLE:
                shapeTypeClass = ComponentCircle.class;
                addComponent(new ComponentCircle());
                break;
            case BOX:
                shapeTypeClass = ComponentBox.class;
                addComponent(new ComponentBox());
                break;
            case TRIANGLE:
                shapeTypeClass = ComponentTriangle.class;
                addComponent(new ComponentTriangle());
                break;
            case STAR:
                shapeTypeClass = ComponentStar.class;
                addComponent(new ComponentStar());
                break;
            default:
                shapeTypeClass = ComponentCircle.class;
                addComponent(new ComponentCircle());
                IO.println("WARNING: Unrecognised shape type. Defaulting to circle");
                break;
        }
    }

    // Getters
    public Vector3f getColour() {return this.colour;}
    public Transform2D getTransform() {return this.transform;}
    public InputStampShapes.SHAPES getShapeType() {return this.shapeType;}
    public float getBlend() { return this.blending.getBlend(); }
    public int getShapeMode() {return shapeModes.ordinal();}

}
