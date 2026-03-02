package Rendering.Objects.Components;

import org.joml.Vector2f;
import org.joml.Vector2i;

public abstract class ComponentTransform extends Component {
    protected Vector2i position;
    protected float rotation = 0f;
    protected float scale = 1.0f;

    public ComponentTransform() {
    }

    // Setters for tweaking
    public void setPosition(Vector2i position) {this.position = position;}
    public void setRotation(float rotation) {this.rotation = rotation;}
    public void setScale(float scale) {this.scale = scale;}
}
