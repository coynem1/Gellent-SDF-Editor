package Jade;

public abstract class Scene {
    private String name;

    public Scene(String name) {
        name = name;
    }

    public abstract void update(float delta);
}
