package util;

import Input.InputShapes;
import Rendering.Objects.Components.Transform2D;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateSDFTest {
    private float radius = 10f;
    private Shape circle;

    @BeforeEach
    void setUpShape() {
        circle = new Shape(InputShapes.SHAPES.CIRCLE, new SculptObject());
        circle.addComponent(Transform2D.createFloat());
        Transform2D<Vector2f> transform = circle.getComponent(Transform2D.class);

        assertNotNull(transform, "Shape should have a transform component but it doesn't");
        transform.setScale(radius);
    }

    @Test
    void CheckCircleSDFDistanceIsEqualToNegativeRadiusAtCenter() {
        float distance = CalculateSDF.calculateSDF(circle, new Vector2f(0,0));
        assertEquals(-radius, distance, "SDF distance measured at the center of the circle should be negative of the radius");
    }

    @Test
    void CheckCircleSDFDistanceIsZeroAtBorder() {
        float distance = CalculateSDF.calculateSDF(circle, new Vector2f(0,radius));
        assertEquals(0, distance, "SDF distance measured at the border of the circle should be zero");
    }

    @Test
    void CheckCircleSDFDistanceIsPositiveOutsideBorder() {
        float distance = CalculateSDF.calculateSDF(circle, new Vector2f(0,radius * 2));
        assertTrue(distance > 0, "SDF distance measured outside the border of the circle should be greater than zero");
    }
}