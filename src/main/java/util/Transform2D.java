package util;

import org.joml.Vector2f;
import org.joml.Vector2i;

// General transform
public class Transform2D <T>{
    protected T position;
    protected float rotation = 0f;
    protected float scale = 1.0f;

    // Factory constructors to choose a vector type
    private Transform2D() {}

    public static Transform2D<Vector2f> createFloat() {
        return new Transform2D<>();
    }

    public static Transform2D<Vector2i> createInt() {
        return new Transform2D<>();
    }

    // Setters for tweaking
    public void setPosition(T position) {this.position = position;}
    public void setRotation(float rotation) {this.rotation = rotation;}
    public void setScale(float scale) {this.scale = scale;}

    // Getters
    public T getPosition() {return this.position;}
    public float getRotation() {return this.rotation;}
    public float getScale() {return this.scale;}
}
