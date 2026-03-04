package Rendering.Objects.Components;

public class ComponentBox extends Component {
    public static final String DEFAULT_NAME = "Box";
    public static final float DEFAULT_LENGTH = 1.0f;
    protected float width = DEFAULT_LENGTH, height = DEFAULT_LENGTH;

    public void setDimensions(float width, float height) {this.width = width; this.height = height;}
}
