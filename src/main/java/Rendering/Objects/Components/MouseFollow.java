package Rendering.Objects.Components;

import Input.InputMouseEvents;
import org.joml.Vector2i;
import util.Transform2D;

public class MouseFollow extends Component {
    private final Transform2D<Vector2i> transform = Transform2D.createInt();

    public MouseFollow() {}

    @Override
    public void start() {
        Vector2i mousePos = InputMouseEvents.getMousePos();

        transform.setPosition(new Vector2i(mousePos.x, mousePos.y));
        IO.println("Pos: "+ transform.getPosition());

    }
}
