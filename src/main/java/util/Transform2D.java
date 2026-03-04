package util;

import org.joml.Vector2i;

public class Transform2D {
    protected Vector2i position;
    protected float rotation = 0f;
    protected float scale = 1.0f;

    // Setters for tweaking
    public void setPosition(Vector2i position) {this.position = position;}
    public void setRotation(float rotation) {this.rotation = rotation;}
    public void setScale(float scale) {this.scale = scale;}

    // Getters
    public Vector2i getPosition() {return this.position;}
    public float getRotation() {return this.rotation;}
    public float getScale() {return this.scale;}
}
