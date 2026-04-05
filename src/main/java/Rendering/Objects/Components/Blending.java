package Rendering.Objects.Components;

import Observers.InputShapesEvents;

import static java.lang.Math.min;

public class Blending extends Component{
    public static final float MAX_BLEND = 40.0f;
    public static final float DEFAULT_BLEND = 0.0f;
    protected float blend = DEFAULT_BLEND;

    public void setBlend(float blend) {
        this.blend = min(blend, MAX_BLEND);
        InputShapesEvents.setBlendCallback(this.blend);
    }
    public float getBlend() {return this.blend;}

    @Override
    public Component copy() {
        Blending copy = new Blending();
        copy.blend = blend;
        return copy;
    }
}
