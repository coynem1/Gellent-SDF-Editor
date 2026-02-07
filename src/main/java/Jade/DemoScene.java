package Jade;

import Input.InputEvents;
import Rendering.RenderDebugger;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import util.Time;

import java.nio.file.Paths;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUseProgram;

// Scene filled with different tweakable shapes
public class DemoScene extends Scene {
    private int currentDemo;
    private String[] demos;

    // Game variables
    private float blend = 0.5f;
    private int toggleRender = 0;
    private boolean blendPressed = false;

    public DemoScene(String name) {
        super(name);

        // File paths
        this.vShaderPath.put(RENDER_SDF, Paths.get("assets/shaders/vertexDemo.glsl"));
        this.fShaderPath.put(RENDER_SDF, Paths.get("assets/shaders/fragmentDemo.glsl"));
        this.vShaderPath.put(RENDER_DEBUG, Paths.get("assets/shaders/debugVertex.glsl"));
        this.fShaderPath.put(RENDER_DEBUG, Paths.get("assets/shaders/debugFragment.glsl"));

        this.currentDemo = 0;
        this.demos = new String[]{"Circle", "Square", "Blending", "MultipleShapes", "Hi Text"};

        // Renderers
        this.render = new RenderSDF(this.vShaderPath.get(RENDER_SDF), this.fShaderPath.get(RENDER_SDF), this.camera);
        this.renderDebugger = new RenderDebugger(this.vShaderPath.get(RENDER_DEBUG), this.fShaderPath.get(RENDER_DEBUG), this.camera);

        // Shaders
        this.shaders.put(RENDER_SDF, this.render.getShader());
        this.shaders.put(RENDER_DEBUG, this.renderDebugger.getShader());

        // Drawing shapes
        this.renderDebugger.createRect(0f, 1f, 100f, 100f);
        this.renderDebugger.createRect(102f, 52f, 60f, 23f);
        this.renderDebugger.render();

        // inputs
        bindInputs();
    }

    // Single binding when key changes
    private void bindInputs() {
        KeyListener.onKeyPressed((key, scancode, mods) -> {
            if (key == GLFW_KEY_0) {
                currentDemo = 0;
            }
            else if (key == GLFW_KEY_1) {
                currentDemo = 1;
            }
            else if (key == GLFW_KEY_2) {
                currentDemo = 2;
            }
            else if (key == GLFW_KEY_3) {
                currentDemo = 3;
            }
            else if (key == GLFW_KEY_4) {
                currentDemo = 4;
            }
        });
    }

    // Sends variables to shader
    private void uploadShader() {
        // glUseProgram(programId);

        shaders.get(RENDER_SDF).uploadMat4("uProjection", camera.getProjectionMat());
        shaders.get(RENDER_SDF).uploadMat4("uView", camera.getViewMat());

        shaders.get(RENDER_SDF).uploadVec2i("uResolution", new Vector2i(Window.get().getWidth(), Window.get().getHeight()));
        shaders.get(RENDER_SDF).uploadVec2f("uCamPos", camera.getPosition());
        shaders.get(RENDER_SDF).uploadFloat("uZoom", 1.0f);

        shaders.get(RENDER_DEBUG).uploadMat4("uProjection", camera.getProjectionMat());
        shaders.get(RENDER_DEBUG).uploadMat4("uView", camera.getViewMat());




        shaders.get(RENDER_SDF).uploadFloat("uTime", Time.getTime());
        shaders.get(RENDER_SDF).uploadFloat("uBlend", blend);
        shaders.get(RENDER_SDF).uploadInt("uDemoScene", currentDemo);
        shaders.get(RENDER_SDF).uploadInt("uToggleRender", toggleRender);
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
                camera.setPosition(new Vector2f(blend, 0f));
            }
            blendPressed = true;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            if (!blendPressed) {
                blend -= 4.0f;
                camera.setPosition(new Vector2f(blend, 0f));
            }
            blendPressed = true;
        } else {
            blendPressed = false;
        }

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



        shaders.get(RENDER_SDF).run();
        render.process(delta);

        shaders.get(RENDER_DEBUG).run();
        renderDebugger.process(delta);

        uploadShader();
    }

}
