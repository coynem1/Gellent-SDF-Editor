package Jade;

import Rendering.Shader;
import org.joml.Vector2i;
import util.Time;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.*;

public class DemoScene extends Scene {
    private Path vertexShaderPath = Paths.get("assets/shaders/vertexDemo.glsl");
    private Path fragmentShaderPath = Paths.get("assets/shaders/fragmentDemo.glsl");

    private String name;
    private int currentDemo;
    private String[] demos;
    private RenderSDF render;
    private Shader shaderSDF;
    private Camera camera;

    private float blend = 0.5f;
    private int toggleRender = 0;
    private boolean blendPressed = false;

    public DemoScene(String name) {
        super(name);

        this.name = name;
        this.currentDemo = 0;
        this.demos = new String[]{"Circle", "Square", "Blending", "MultipleShapes", "Hi Text"};
        this.render = new RenderSDF(vertexShaderPath, fragmentShaderPath);
        this.shaderSDF = this.render.getShader();
        this.camera = render.getCamera();
    }

    // Sends variables to shader
    private void uploadShader() {
        shaderSDF.uploadMat4("uProjection", camera.getProjectionMat());
        shaderSDF.uploadMat4("uView", camera.getViewMat());

        shaderSDF.uploadFloat("uTime", Time.getTime());
        shaderSDF.uploadFloat("uBlend", blend);
        shaderSDF.uploadInt("uDemoScene", currentDemo);
        shaderSDF.uploadInt("uToggleRender", toggleRender);
    }

    @Override
    public void process(float delta) {
        // changes render to use
        if (KeyListener.isKeyPressed(GLFW_KEY_0)) {
            currentDemo = 0;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_1)) {
            currentDemo = 1;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_2)) {
            currentDemo = 2;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_3)) {
            currentDemo = 3;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_4)) {
            currentDemo = 4;
        }

        // Change Blend
        if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            if (!blendPressed) {
                blend += 4.0f;
            }
            blendPressed = true;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            if (!blendPressed) {
                blend -= 4.0f;
            }
            blendPressed = true;
        } else {
            blendPressed = false;
        }
        IO.println("Blend: " + blend);

        // Change render mode
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            toggleRender = 0;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            toggleRender = 1;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            toggleRender = 2;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_F)) {
            toggleRender = 3;
        }

        uploadShader();
        render.process(delta);
    }

    // Debugging purposes only, delete if un-needed
    private void printDemoName() {
        IO.println("Demo " + name + ": " + demos[currentDemo]);
    }

}
