package Rendering.Objects.Components;

import Input.InputMouseEvents;
import org.joml.Vector2i;

public class ComponentShapeTransform extends ComponentTransform {
    private Vector2i mousePos = new Vector2i();

    public ComponentShapeTransform() {}

    @Override
    public void start() {
        setPosition(mousePos);
    }

    private void bindInputs() {
        InputMouseEvents.onMove((xPos, yPos, _, _) -> {
            mousePos = new Vector2i(xPos, yPos);
        });
    }
}
