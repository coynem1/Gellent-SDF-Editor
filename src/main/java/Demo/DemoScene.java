package Demo;

import Observers.InputKeyEvents;
import Input.InputShapes;
import Jade.Scene;
import Jade.Window;
import Rendering.Objects.Components.ComponentRounded;
import Rendering.Objects.GameObject;
import Rendering.Objects.SculptObject;
import Rendering.Objects.Shape;
import Rendering.RenderDebugger;
import Rendering.RenderSDF;
import org.joml.Vector2f;
import org.joml.Vector2i;
import util.Time;
import Rendering.Objects.Components.Transform2D;

import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

// Scene filled with different tweakable shapes
public class DemoScene extends Scene {
    private int currentDemo;
    private String[] demos;

    // Game variables
    private int toggleRender = 0;

    private Shape testObject;

    public DemoScene() {
        super();

        // File paths
        // this.vShaderPath.put(RENDER_SDF, Paths.get("assets/shaders/vertexDemo.glsl"));
        // this.fShaderPath.put(RENDER_SDF, Paths.get("assets/shaders/fragmentEditor.glsl"));
        // this.vShaderPath.put(RENDER_DEBUG, Paths.get("assets/shaders/debugVertex.glsl"));
        // this.fShaderPath.put(RENDER_DEBUG, Paths.get("assets/shaders/debugFragment.glsl"));

        this.currentDemo = 0;
        this.demos = new String[]{"Circle", "Square", "Blending", "MultipleShapes", "Hi Text"};

        // Renderers
        this.render = new RenderSDF(this.vShaderPath.get(RENDER_SDF), this.fShaderPath.get(RENDER_SDF), this.camera);
        this.renderDebugger = new RenderDebugger(this.vShaderPath.get(RENDER_DEBUG), this.fShaderPath.get(RENDER_DEBUG), this.camera);

        // Shaders
        this.shaders.put(RENDER_SDF, this.render.getShader());
        this.shaders.put(RENDER_DEBUG, this.renderDebugger.getShader());

        // Drawing shapes
        this.renderDebugger.createRect(-50f, -50f, 100f, 100f);
        this.renderDebugger.render();

        bindInputs();
        uploadShader();

        // Draw shapes
        testSculpt();
    }

    private void testSculpt() {
        SculptObject sculpt = new SculptObject();
        testObject = new Shape(InputShapes.SHAPES.BOX, sculpt);

        this.addObjectToScene(testObject);

        Transform2D<Vector2f> transform = testObject.getTransform();
        transform.setScale(Transform2D.DEFAULT_SCALE);
        transform.setRotation(45f);
        transform.setPosition(new Vector2f(0, 0));
        testObject.setTransform(transform);
    }

    // Single binding when key changes
    private void bindInputs() {
        InputKeyEvents.onKeyPressed((key, scancode, mods) -> {
            switch (key) {
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
                case GLFW_KEY_G:
                    toggleRender = 3;
                    break;
            }
        });


    }

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

    private void uploadShapes() {
        int MAX_SHAPES = 100;
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
            uShapePos[i] = transform.getPosition();
            uShapeTypes[i] = shape.getShapeType().ordinal();
            uShapeModes[i] = shape.getShapeMode();
            uShapeSizes[i] = transform.getScale();
            uShapeAngles[i] = transform.getRotation();
            uShapeBlends[i] = shape.getBlend();


            ComponentRounded rounded = shape.getComponent(ComponentRounded.class);
            if (rounded == null) uShapeRounds[i] = 0f;
            else uShapeRounds[i] = rounded.getRounded();

        }

        if (uShapeCount == 0) return;

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

    @Override
    public void process(float delta) {
        uploadShader();
        uploadShaderImmediate();

        shaders.get(RENDER_SDF).run();
        render.process(delta);

        shaders.get(RENDER_DEBUG).run();
        renderDebugger.process(delta);
    }

}
