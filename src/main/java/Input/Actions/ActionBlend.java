package Input.Actions;

import Rendering.Objects.Components.Blending;
import Rendering.Objects.Components.Transform2D;
import Rendering.Objects.GameObject;
import Rendering.Objects.Shape;
import org.joml.Vector2f;

public class ActionBlend implements Action {
    GameObject object;
    float blendA, blendB;

    public ActionBlend(GameObject object, float blendA, float blendB) {
        this.object = object;
        this.blendA = blendA;
        this.blendB = blendB;
    }

    @Override public void execute() {
        Blending blending = object.getComponent(Blending.class);
        if (blending == null) {
            if (object.getClass() == Shape.class) ((Shape) object).setBlend(blendB);
            return;
        }

        blending.setBlend(blendB);
    }
    @Override public void undo() {
        Blending blending = object.getComponent(Blending.class);
        if (blending == null) {
            if (object.getClass() == Shape.class) ((Shape) object).setBlend(blendA);
            return;
        }
        blending.setBlend(blendA);
    }
}
