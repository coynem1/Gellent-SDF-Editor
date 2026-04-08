package Rendering.Objects.Components;

import Observers.InputShapesEvents;

import static java.lang.Math.min;

public class ComponentRounded extends Component {
    public static transient final float MAX_ROUNDED = 0.6f;
    protected float rounded = 0.0f;

    public void setRounded(float round) {
        this.rounded = min(round, MAX_ROUNDED);
        InputShapesEvents.setRoundCallback(rounded);
    }
    public float getRounded() { return rounded; }

    @Override
    public Component copy() {
        ComponentRounded copy = new ComponentRounded();
        copy.rounded = rounded;
        return copy;
    }
}
