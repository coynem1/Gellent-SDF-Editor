package Jade;

public class Scene {
    private String name;
    private RenderSDF render;

    public Scene(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void process(float delta) {}


}
