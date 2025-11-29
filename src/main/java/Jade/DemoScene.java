package Jade;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DemoScene extends Scene {
    private String name;
    private int currentDemo;
    private String[] demos;
    private ScreenRender render;

    public DemoScene(String name) {
        this.name = name;
        this.currentDemo = 0;
        this.demos = new String[]{"Circle", "MultipleShapes", "BlendShapes", "Cutting"};
        this.render = new ScreenRender();
        super(name);
    }

    @Override
    public void process(float delta) {
        render.process(delta);
    }

    // Custom circle level
    private void circle() {
        IO.println("Hello World " + name + " " + demos[currentDemo]);
    }

}
