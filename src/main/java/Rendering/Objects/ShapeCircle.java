package Rendering.Objects;

public class ShapeCircle extends Shape {
    public static final String DEFAULT_NAME = "Circle";
    public static final float DEFAULT_RADIUS = 1.0f;
    protected float radiusMajor = DEFAULT_RADIUS, radiusMinor = DEFAULT_RADIUS;

    public ShapeCircle(String name) {
        super(name);
    }

    public void setRadii(float radiusMajor, float radiusMinor) {this.radiusMajor = radiusMajor; this.radiusMinor = radiusMinor;}
}
