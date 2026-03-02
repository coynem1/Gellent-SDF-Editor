package Rendering.Objects.Components;

import Rendering.Objects.GameObject;

public abstract class Component {
    public GameObject gameObject = null;

    public void start() {}
    public void update(float delta) {}
}
