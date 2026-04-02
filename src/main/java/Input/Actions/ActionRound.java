package Input.Actions;

import Rendering.Objects.Components.Blending;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GameObject;

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
        if (rounding == null) return;

        rounding.setRounded(roundB);
    }
    @Override public void undo() {
        ComponentRounded rounding = object.getComponent(ComponentRounded.class);
        if (rounding == null) return;

        rounding.setRounded(roundA);
    }
}
