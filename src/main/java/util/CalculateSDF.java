package util;

import Rendering.Objects.Components.Transform2D;
import Rendering.Objects.Shape;
import org.joml.Vector2f;

import static java.lang.Math.*;

public abstract class CalculateSDF {
    private static final float BIG_NUMBER = 1e10f;
    private static final float TWO = 2f;
    private static final float SQRT_THREE = (float) sqrt(3f);

    // Star
    private static final float k1x = (float) cos(PI / 5);
    private static final float k2x = (float) sin(PI / 10);
    private static final float k1y = (float) sin(PI / 5);
    private static final float k2y = (float) cos(PI / 10);
    private static final float k1z = (float) tan(PI / 5);
    private static final Vector2f v1  = new Vector2f( k1x,-k1y);
    private static final Vector2f v2  = new Vector2f(-k1x,-k1y);
    private static final Vector2f v3  = new Vector2f( k2x,-k2y);

    public static float calculateSDF(Shape shape, Vector2f pos) {
        Transform2D<Vector2f> transform = shape.getComponent(Transform2D.class);
        if (transform == null) return BIG_NUMBER;

        // Translate the point to local space
        Vector2f p = rotate(pos.sub(transform.getPosition(), new Vector2f()), transform.getRotation());
        float size = transform.getScale();

        switch (shape.getShapeType()) {
            case CIRCLE:
                return circleSDF(p, size);
            case BOX:
                return boxSDF(p, new Vector2f(size, size));
            case TRIANGLE:
                return equilateralTriangleSDF(p, size);
            case STAR:
                return starSDF(p, size);
            default:
                return BIG_NUMBER;
        }
    }

    // Get a rough distance between the shape and a point
    private static float circleSDF(Vector2f p, float radius) {
        return (p.length() - radius);
    }

    private static float boxSDF(Vector2f p, Vector2f size) {
        Vector2f d = p.absolute(new Vector2f()).sub(size);
        return new Vector2f(max(d.x, 0f), max(d.y, 0f)).length() + (float) min(max(d.x,d.y),0.0);
    }

    private static float equilateralTriangleSDF(Vector2f p, float radius) {
        p.x = abs(p.x) - radius;
        p.y = p.y + radius / SQRT_THREE;
        if( p.x + SQRT_THREE * p.y > 0f) {
            p = new Vector2f(p.x - SQRT_THREE * p.y,- SQRT_THREE * p.x - p.y).div(TWO);
        }
        p.x -= clamp( p.x, -TWO * radius, 0f);
        return -p.length() * signum(p.y);
    }

    private static float starSDF(Vector2f p, float radius) {
        p.x = abs(p.x);
        p = p.sub(v1.mul(2f * max(p.dot(v1), 0f), new Vector2f()), new Vector2f());
        p = p.sub(v2.mul(2f * max(p.dot(v2), 0f), new Vector2f()), new Vector2f());
        p.x = abs(p.x);
        p.y -= radius;

        return (p.sub(v3.mul(clamp(p.dot(v3), 0f, k1z * radius),
                new Vector2f()), new Vector2f())).length()
                * signum(p.y * v3.x - p.x * v3.y);
    }

    // Rotate a point around the origin
    private static Vector2f rotate(Vector2f p, float angle) {
        float cos = (float) Math.cos(-angle);
        float sin = (float) Math.sin(-angle);
        p = new Vector2f(cos * p.x - sin * p.y, sin * p.x + cos * p.y);
        return p;
    }
}
