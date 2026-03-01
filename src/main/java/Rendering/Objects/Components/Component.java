package Rendering.Objects.Components;

import Rendering.Objects.GameObject;

public abstract class Component {
    public GameObject gameObject = null;

    public abstract void start();
    public void update(float delta) {}
}
