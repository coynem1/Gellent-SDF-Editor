package Rendering.Objects.Components;

public class ComponentTriangle extends Component {
    public transient static final String DEFAULT_NAME = "Triangle";
    public transient static final float DEFAULT_DIMENSIONS = 1.0f;
    protected float width = DEFAULT_DIMENSIONS, height = DEFAULT_DIMENSIONS;

    public void setDimensions(float width, float height) {this.width = width; this.height = height;}
}
