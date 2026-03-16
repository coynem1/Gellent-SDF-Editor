package Rendering.Objects.Components;

public class BasicStar extends Component implements BasicShape {
    public transient static final String DEFAULT_NAME = "Star";
    public transient static final float DEFAULT_DIMENSIONS = 1.0f;
    protected float width = DEFAULT_DIMENSIONS, height = DEFAULT_DIMENSIONS;

    public void setDimensions(float width, float height) {this.width = width; this.height = height;}
}
