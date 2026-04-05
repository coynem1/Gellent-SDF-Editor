package Input.Actions;

import Rendering.Objects.Components.Blending;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GameObject;
import Rendering.Objects.Shape;

public class ActionRound implements Action {
    GameObject object;
    float roundA, roundB;

    public ActionRound(GameObject object, float roundA, float roundB) {
        this.object = object;
        this.roundA = roundA;
        this.roundB = roundB;
    }

    @Override public void execute() {
        ComponentRounded rounding = object.getComponent(ComponentRounded.class);
        if (rounding == null) {
            if (object.getClass() == Shape.class) ((Shape) object).setRounded(roundB);
            return;
        }

        rounding.setRounded(roundB);
    }
    @Override public void undo() {
        ComponentRounded rounding = object.getComponent(ComponentRounded.class);
        if (rounding == null) {
            if (object.getClass() == Shape.class) ((Shape) object).setRounded(roundA);
            return;
        }

        rounding.setRounded(roundA);
    }
}
