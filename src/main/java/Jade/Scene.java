package Jade;

import Rendering.RenderDebugger;
import Rendering.Shader;
import org.joml.Vector2f;

import java.nio.file.Path;
import java.util.HashMap;

public class Scene {
    // Path keys for different shaders
    protected final String RENDER_SDF = "RENDER_SDF";
    protected final String RENDER_DEBUG = "RENDER_DEBUG";

    protected String name;
    protected RenderSDF render;
    protected RenderDebugger renderDebugger;
    protected Camera camera;

    protected HashMap<String, Shader> shaders;
    protected Shader shaderSDF;
    protected HashMap<String, Path> vShaderPath;
    protected HashMap<String, Path> fShaderPath;

    public Scene(String name) {
        this.name = name;
        this.vShaderPath = new HashMap<String, Path>();
        this.fShaderPath = new HashMap<String, Path>();
        this.shaders = new HashMap<String, Shader>();
        this.camera = new Camera(new Vector2f());
    }

    public void process(float delta) {
        camera.process();
    }

    public String getName() {
        return name;
    }

    public Camera getCamera() {return this.camera;}
}
