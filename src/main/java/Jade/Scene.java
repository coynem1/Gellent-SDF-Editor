package Jade;

import Rendering.Objects.Components.Component;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GameObject;
import Rendering.Objects.Shape;
import Rendering.RenderDebugger;
import Rendering.RenderSDF;
import Rendering.Shaders.Shader;
import Saving.Deserialisers.DeserialiseComponents;
import Saving.Deserialisers.DeserialiseGameObjects;
import Saving.Deserialisers.DeserialiseShapes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import util.GameClock;
import util.Time;
import Rendering.Objects.Components.Transform2D;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Scene {
    // Path keys for different shaders
    protected static final String RENDER_SDF = "RENDER_SDF";
    protected static final String RENDER_DEBUG = "RENDER_DEBUG";
    protected static final String DEFAULT_SCENE_NAME = "Unnamed Scene";
    protected static final int MAX_SHAPES = 145;

    protected String name;
    protected RenderSDF render;
    protected RenderDebugger renderDebugger;
    protected Camera camera;

    protected HashMap<String, Shader> shaders;
    protected HashMap<String, Path> vShaderPath;
    protected HashMap<String, Path> fShaderPath;

    // Object handling
    protected ArrayList<GameObject> objects = new ArrayList<>();
    protected boolean isRunning = false;
    protected boolean levelLoaded = false;
    protected boolean awaitGameClock = false;
    protected int toggleRender = 0;


    public Scene() {
        this.name = DEFAULT_SCENE_NAME;
        this.vShaderPath = new HashMap<String, Path>();
        this.fShaderPath = new HashMap<String, Path>();
        this.shaders = new HashMap<String, Shader>();
        this.camera = new Camera(new Vector2f());

        // Default file paths
        this.vShaderPath.put(RENDER_SDF, Paths.get("assets/shaders/vertexDemo.glsl"));
        this.fShaderPath.put(RENDER_SDF, Paths.get("assets/shaders/fragmentEditor.glsl"));
        this.vShaderPath.put(RENDER_DEBUG, Paths.get("assets/shaders/debugVertex.glsl"));
        this.fShaderPath.put(RENDER_DEBUG, Paths.get("assets/shaders/debugFragment.glsl"));

        // Renderers
        this.render = new RenderSDF(this.vShaderPath.get(RENDER_SDF), this.fShaderPath.get(RENDER_SDF), this.camera);
        this.renderDebugger = new RenderDebugger(this.vShaderPath.get(RENDER_DEBUG), this.fShaderPath.get(RENDER_DEBUG), this.camera);

        // Shaders
        this.shaders.put(RENDER_SDF, this.render.getShader());
        this.shaders.put(RENDER_DEBUG, this.renderDebugger.getShader());

        // Upload shaders in fixed intervals
        GameClock.get().addObserver(delta -> {
            // Cannot change glfw not on the main thread
            awaitGameClock = true;
        });
    }

    // Load JSON and create objects from it
    protected void loadSceneFromFile(@NotNull Path path) {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Component.class, new DeserialiseComponents())
            .registerTypeAdapter(GameObject.class, new DeserialiseGameObjects())
            .registerTypeAdapter(Shape.class, new DeserialiseShapes())
            .create();

        // IO.println("Loading scene from file: " + path.toString());
        String json = "";
        try {
            json = new String(Files.readAllBytes(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json.isEmpty()) SceneManager.get().loadScene(null);

        try {
            // GameObject[] objs = gson.fromJson(json, GameObject[].class);
            Shape[] objs = gson.fromJson(json, Shape[].class);
            for (GameObject obj : objs) {
                addObjectToScene(obj);
            }
            levelLoaded = true;
        } catch (Exception e) {
            // Load a blank scene if an error occurs
            SceneManager.get().loadScene(null);
        }
    }

    // Start all objects in the scene
    public void start() {
        if (isRunning) {return;}
        isRunning = true;

        for (GameObject obj : objects) {
            obj.start();
        }
        // levelLoaded = true;
    }

    // Update all objects in the scene
    public void updateAllObjects(float delta) {
        for (GameObject obj : objects) {
            obj.update(delta);
        }
    }

    public void addObjectToScene(GameObject object) { addObjectToScene(object, objects.size()); }
    public void addObjectToScene(GameObject object, int index) {
        int i = index;
        if (objects.size() >= MAX_SHAPES -1) {
            removeObjectFromScene(objects.get(0));
            i -= 1;
        }
        objects.add(i, object);
        if (isRunning) { object.start(); }
    }

    public void removeObjectFromScene(GameObject object) { objects.remove(object); }

    // Sends variables to shader at fixed intervals
    private void uploadShader() {
        if (!awaitGameClock) return;
        awaitGameClock = false;

        uploadShapes();

        shaders.get(RENDER_SDF).uploadMat4("uProjection", camera.getStaticProjectionMat());
        shaders.get(RENDER_SDF).uploadMat4("uView", camera.getViewMat(true));

        shaders.get(RENDER_SDF).uploadVec2i("uResolution", new Vector2i(Window.get().getWidth(), Window.get().getHeight()));
        shaders.get(RENDER_SDF).uploadFloat("uViewHeight", camera.getViewHeight());

        shaders.get(RENDER_SDF).uploadFloat("uTime", Time.getTime());
        shaders.get(RENDER_SDF).uploadInt("uToggleRender", toggleRender);
    }

    // TODO: Optimise to update only shapes that have changed
    private void uploadShapes() {
        int uShapeCount = 0;
        Vector2f[] uShapePos = new Vector2f[MAX_SHAPES];
        int[] uShapeTypes = new int[MAX_SHAPES];
        int[] uShapeModes = new int[MAX_SHAPES];
        float[] uShapeSizes = new float[MAX_SHAPES];
        float[] uShapeAngles = new float[MAX_SHAPES];
        float[] uShapeBlends = new float[MAX_SHAPES];
        float[] uShapeRounds = new float[MAX_SHAPES];


        for (int i = 0; i < objects.size(); i++) {
            GameObject obj = objects.get(i);
            if (!(obj instanceof Shape)) return;

            Shape shape = (Shape) obj;
            Transform2D<Vector2f> transform = shape.getTransform();

            uShapeCount ++;

            uShapePos[i] = new Vector2f(transform.getPosition().x,transform.getPosition().y);
            uShapeTypes[i] = shape.getShapeType().ordinal();
            uShapeModes[i] = shape.getShapeMode();
            uShapeSizes[i] = transform.getScale();
            uShapeAngles[i] = transform.getRotation();
            uShapeBlends[i] = shape.getBlend();

            ComponentRounded rounded = shape.getComponent(ComponentRounded.class);
            if (rounded == null) uShapeRounds[i] = 0f;
            else uShapeRounds[i] = rounded.getRounded();

        }

        shaders.get(RENDER_SDF).uploadInt("uShapeCount", uShapeCount);
        shaders.get(RENDER_SDF).uploadVec2f("uShapePos", uShapePos, uShapeCount);
        shaders.get(RENDER_SDF).uploadInt("uShapeTypes", uShapeTypes);
        shaders.get(RENDER_SDF).uploadInt("uShapeModes", uShapeModes);
        shaders.get(RENDER_SDF).uploadFloat("uShapeSizes", uShapeSizes);
        shaders.get(RENDER_SDF).uploadFloat("uShapeAngles", uShapeAngles);
        shaders.get(RENDER_SDF).uploadFloat("uShapeBlends", uShapeBlends);
        shaders.get(RENDER_SDF).uploadFloat("uShapeRounds", uShapeRounds);

    }

    // Sends variables to shader every frame
    private void uploadShaderImmediate() {
        shaders.get(RENDER_SDF).uploadVec2f("uCamPos", camera.getPosition());
        shaders.get(RENDER_SDF).uploadFloat("uZoom", camera.getZoom());

        shaders.get(RENDER_DEBUG).uploadMat4("uProjection", camera.getProjectionMat());
        shaders.get(RENDER_DEBUG).uploadMat4("uView", camera.getViewMat(false));
    }

    public void process(float delta) {
        uploadShader();
        uploadShaderImmediate();

        shaders.get(RENDER_SDF).run();
        render.process(delta);

        shaders.get(RENDER_DEBUG).run();
        renderDebugger.process(delta);
    }

    public Camera getCamera() {return camera;}
    public ArrayList<GameObject> getObjects() {return objects;}
    public String getName() {return name;}
}
