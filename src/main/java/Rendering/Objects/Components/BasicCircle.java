package Rendering.Objects.Components;

public class BasicCircle extends Component implements BasicShape {
    public static final String DEFAULT_NAME = "Circle";
    public static final float DEFAULT_RADIUS = 1.0f;
    protected float radiusMajor = DEFAULT_RADIUS, radiusMinor = DEFAULT_RADIUS;

    public void setDimensions(float radiusMajor, float radiusMinor) {this.radiusMajor = radiusMajor; this.radiusMinor = radiusMinor;}
}
