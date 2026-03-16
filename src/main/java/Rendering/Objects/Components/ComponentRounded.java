package Rendering.Objects.Components;

public class ComponentRounded extends Component {
    public static transient final float MAX_ROUNDED = 0.6f;
    protected float rounded = 0.0f;

    public void setRounded(float round) { this.rounded = round; }
    public float getRounded() { return rounded; }
}
