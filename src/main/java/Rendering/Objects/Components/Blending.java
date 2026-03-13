package Rendering.Objects.Components;

public class Blending extends Component{
    public static final float DEFAULT_BLEND = 0.0f;
    protected float blend = DEFAULT_BLEND;

    public void setBlend(float blend) {this.blend = blend;}
    public float getBlend() {return this.blend;}
}
