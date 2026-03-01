package Rendering.Objects.Components;

import org.joml.Vector2f;

public class ComponentTransform extends Component {
    protected Vector2f position;
    protected float rotation = 0f;
    protected float scale = 1.0f;

    public ComponentTransform() {
    }

    @Override
    public void start() {
    }

    // Setters for tweaking
    public void setPosition(Vector2f position) {this.position = position;}
    public void setRotation(float rotation) {this.rotation = rotation;}
    public void setScale(float scale) {this.scale = scale;}
}
