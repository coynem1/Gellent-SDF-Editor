package Jade;

public class DemoScene extends Scene {
    private String name;
    private int currentDemo;
    private String[] demos;

    public DemoScene(String name) {
        this.name = name;
        this.currentDemo = 0;
        this.demos = new String[]{"Circle", "MultipleShapes", "BlendShapes", "Cutting"};
        super(name);
    }

    @Override
    public void process(float delta) {
        IO.println("Hello World " + name + " " + delta );
    }

}
