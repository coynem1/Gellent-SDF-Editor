package Rendering.Objects.Components;

import Rendering.Objects.GameObject;

public abstract class Component {
    public transient GameObject gameObject = null;

    public void start() {}
    public void update(float delta) {}
    public abstract Component copy();
}
