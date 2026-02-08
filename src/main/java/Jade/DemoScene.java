package Jade;

import Input.InputKeyEvents;
import Rendering.RenderDebugger;
import org.joml.Vector2i;
import util.Time;

import java.nio.file.Paths;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

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

        bindInputs();
    }

    // Single binding when key changes
    private void bindInputs() {
        InputKeyEvents.onKeyPressed((key, scancode, mods) -> {
            switch (key) {
                // Change Scene
                case GLFW_KEY_0:
                    currentDemo = 0;
                    break;
                case GLFW_KEY_1:
                    currentDemo = 1;
                    break;
                case GLFW_KEY_2:
                    currentDemo = 2;
                    break;
                case GLFW_KEY_3:
                    currentDemo = 3;
                    break;
                case GLFW_KEY_4:
                    currentDemo = 4;
                    break;

                // Change Blend
                case GLFW_KEY_UP:
                    blend += 0.4f;
                    break;
                case GLFW_KEY_DOWN:
                    blend -= 0.4f;
                    break;

                // Change render mode
                case GLFW_KEY_LEFT:
                    toggleRender = 0;
                    break;
                case GLFW_KEY_RIGHT:
                    toggleRender = 1;
                    break;
                case GLFW_KEY_SPACE:
                    toggleRender = 2;
                    break;
                case GLFW_KEY_F:
                    toggleRender = 3;
                    break;
            }
        });


    }

    // Sends variables to shader
    private void uploadShader() {
        // glUseProgram(programId);

        shaders.get(RENDER_SDF).uploadMat4("uProjection", camera.getStaticProjectionMat());
        shaders.get(RENDER_SDF).uploadMat4("uView", camera.getViewMat(true));

        shaders.get(RENDER_SDF).uploadVec2i("uResolution", new Vector2i(Window.get().getWidth(), Window.get().getHeight()));
        shaders.get(RENDER_SDF).uploadVec2f("uCamPos", camera.getPosition());
        shaders.get(RENDER_SDF).uploadFloat("uZoom", 1.0f);

        shaders.get(RENDER_DEBUG).uploadMat4("uProjection", camera.getProjectionMat());
        shaders.get(RENDER_DEBUG).uploadMat4("uView", camera.getViewMat(false));

        shaders.get(RENDER_SDF).uploadFloat("uTime", Time.getTime());
        shaders.get(RENDER_SDF).uploadFloat("uBlend", blend);
        shaders.get(RENDER_SDF).uploadInt("uDemoScene", currentDemo);
        shaders.get(RENDER_SDF).uploadInt("uToggleRender", toggleRender);
    }

    @Override
    public void process(float delta) {
        camera.process();

        shaders.get(RENDER_SDF).run();
        render.process(delta);

        shaders.get(RENDER_DEBUG).run();
        renderDebugger.process(delta);

        uploadShader();
    }

}
