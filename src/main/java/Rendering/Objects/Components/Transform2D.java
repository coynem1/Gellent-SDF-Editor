package Rendering.Objects.Components;

import org.joml.Vector2f;
import org.joml.Vector2i;

// General transform
public class Transform2D <T> extends Component {
    protected T position;
    protected float rotation = 0f;
    protected float scale = 1.0f;

    // Factory constructors to choose a vector type
    private Transform2D() {}

    public static Transform2D<Vector2f> createFloat() {
        Transform2D<Vector2f> t = new Transform2D<>();
        t.position = new Vector2f(0, 0);
        return t;
    }

    public static Transform2D<Vector2i> createInt() {
        Transform2D<Vector2i> t = new Transform2D<>();
        t.position = new Vector2i(0, 0);
        return t;
    }

    // Copy the transform to a new object
    public Transform2D<T> copy() {
        Transform2D<T> copy = new Transform2D<>();
        copy.rotation = this.rotation;
        copy.scale = this.scale;

        if (this.position instanceof Vector2f v) {
            copy.position = (T) new Vector2f(v);
        } else if (this.position instanceof Vector2i v) {
            copy.position = (T) new Vector2i(v);
        }

        return copy;
    }

    // Copy everything from another transform
    public void copyFrom(Transform2D<T> other) {
        this.rotation = other.rotation;
        this.scale = other.scale;

        if (other.position instanceof Vector2f v) {
            this.position = (T) new Vector2f(v);
        } else if (other.position instanceof Vector2i v) {
            this.position = (T) new Vector2i(v);
        }
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
