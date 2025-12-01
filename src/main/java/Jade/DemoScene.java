package Jade;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class DemoScene extends Scene {
    private String name;
    private int currentDemo;
    private float blend = 0.5f;
    private String[] demos;
    private ScreenRender render;
    private boolean blendPressed = false;

    public DemoScene(String name) {
        this.name = name;
        this.currentDemo = 0;
        this.demos = new String[]{"Circle", "Square", "MultipleShapes", "Hi Text", "BlendShapes", "Cutting"};
        this.render = new ScreenRender();
        super(name);
    }

    @Override
    public void process(float delta) {

        render.process(delta);

        // changes render to use
        if (KeyListener.isKeyPressed(GLFW_KEY_0)) {
            currentDemo = 0;
            render.setDemoScene(currentDemo);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_1)) {
            currentDemo = 1;
            render.setDemoScene(currentDemo);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_2)) {
            currentDemo = 2;
            render.setDemoScene(currentDemo);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_3)) {
            currentDemo = 3;
            render.setDemoScene(currentDemo);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_4)) {
            currentDemo = 4;
            render.setDemoScene(currentDemo);
        }

        // Change Blend
        if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            if (!blendPressed) {
                blend += 4.0f;
                render.setDemoBlend(blend);
            }
            blendPressed = true;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            if (!blendPressed) {
                blend -= 4.0f;
                render.setDemoBlend(blend);
            }
            blendPressed = true;
        } else {
            blendPressed = false;
        }


    }

    // Debugging purposes only, delete if un-needed
    private void printDemoName() {
        IO.println("Demo " + name + ": " + demos[currentDemo]);
    }

}
