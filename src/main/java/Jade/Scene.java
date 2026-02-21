package Jade;

import Rendering.Objects.GameObject;
import Rendering.RenderDebugger;
import Rendering.RenderSDF;
import Rendering.Shaders.Shader;
import org.joml.Vector2f;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Scene {
    // Path keys for different shaders
    protected static final String RENDER_SDF = "RENDER_SDF";
    protected static final String RENDER_DEBUG = "RENDER_DEBUG";

    protected String name;
    protected RenderSDF render;
    protected RenderDebugger renderDebugger;
    protected Camera camera;

    protected HashMap<String, Shader> shaders;
    protected Shader shaderSDF;
    protected HashMap<String, Path> vShaderPath;
    protected HashMap<String, Path> fShaderPath;

    // Object handling
    protected ArrayList<GameObject> objects = new ArrayList<>();
    protected boolean isRunning = false;


    public Scene(String name) {
        this.name = name;
        this.vShaderPath = new HashMap<String, Path>();
        this.fShaderPath = new HashMap<String, Path>();
        this.shaders = new HashMap<String, Shader>();
        this.camera = new Camera(new Vector2f());
    }

    // Start all objects in the scene
    public void start() {
        if (isRunning) {return;}
        isRunning = true;

        for (GameObject obj : objects) {
            obj.start();
        }
    }

    public void addObjectToScene(GameObject object) {
        objects.add(object);

        if (isRunning) { object.start(); }
    }

    public void process(float delta) {
        camera.process();
    }

    // public String getName() { return this.name; }
    // public Camera getCamera() {return this.camera;}
}
